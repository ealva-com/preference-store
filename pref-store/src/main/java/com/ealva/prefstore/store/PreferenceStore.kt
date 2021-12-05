/*
 * Copyright (c) 2021  Eric A. Snell
 *
 * This file is part of PreferenceStore
 *
 * PreferenceStore is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * PreferenceStore is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * PreferenceStore. If not, see <http://www.gnu.org/licenses/>.
 */

package com.ealva.prefstore.store

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ealva.prefstore.store.PreferenceStore.Preference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

public typealias StorePref<S, A> = Preference<S, A>
public typealias Sanitize<T> = ((T) -> T)? // optional conversion function

public interface MutablePreferenceStore {
  public operator fun <S, A> set(preference: StorePref<S, A>, value: A)
  public fun <S, A> clear(preference: StorePref<S, A>)
}

/**
 * Simply contains a PreferenceStore<T>. This is not a data class because we want identity
 * equals and hashCode, and we don't need any extra generated code. This is currently used so
 * that PreferenceStore doesn't need a "correct" equals function and items emitted on
 * [PreferenceStore.updateFlow]
 */
public class StoreHolder<T : PreferenceStore<T>>(public val store: T)

public inline operator fun <T : PreferenceStore<T>> StoreHolder<T>.invoke(
  block: (T) -> Unit
): Unit = block(store)

/**
 * PreferenceStore wraps a DataStore<Preference> and provides a higher level abstraction of a
 * [Preference], getting and setting values, flows of values for a [Preference], and mapping of
 * types used by a client and the type stored in the [DataStore].
 *
 * PreferenceStore is parameterized with the subclass that contains the [Preference] values
 */
public interface PreferenceStore<T : PreferenceStore<T>> {

  /**
   * Whenever this PreferenceStore is updated it is emitted here contained in a [StoreHolder].
   */
  public val updateFlow: Flow<StoreHolder<T>>

  public interface Preference<S, A> : ReadOnlyProperty<PreferenceStore<*>, Preference<S, A>> {

    /** The key used to store and retrieve the preference value */
    public val key: Preferences.Key<S>

    /** Will be returned if the stored preference is null (removed or never set) */
    public val default: A

    /**
     * Sanitize is an optional function that accepts/rejects/maps a value. [actualToStored]
     * will call this function, if not null, before any conversion.
     */
    public val sanitize: Sanitize<A>

    /** Invoke a preference to get its value. */
    public operator fun invoke(): A

    /**
     * Set the Preference to [value]. An edit/commit is performed. If modifying several preferences,
     * use the [edit] function to keep all modification within a single commit.
     */
    public suspend fun set(value: A)

    /**
     * If the stored and actual types differ, this function converts the stored type to the actual
     * type. If stored is null, the [default] is returned. As an example, an enum type may
     * store it's name and this function would convert the name back to the enum instance.
     */
    public fun storedToActual(stored: S?): A

    /**
     * If the actual and stored types differ, this function converts the actual type to the stored
     * type. For example, an enum type may store it's name. If [sanitize] is not null, [actual] will
     * be sanitized before conversion to [S] type.
     */
    public fun actualToStored(actual: A): S

    /**
     * A [Flow] of values from this preference. As values are committed they will be emitted from
     * this flow. Only distinct values are emitted:
     * [distinctUntilChanged](kotlinx.coroutines.flow.distinctUntilChanged)
     */
    public fun asFlow(): Flow<A>
  }

  /**
   * Call the [block] function with a [MutablePreferenceStore] where multiple preference values may
   * be committed as one. This is more performant than calling individual [Preference.set]s as there
   * will be one commit (one write, one emit, etc.) when [block] exits.
   */
  public suspend fun edit(block: suspend T.(MutablePreferenceStore) -> Unit)

  /**
   * Clear [prefs] or clear all if [prefs] is empty. Effectively sets them to their default value
   * for read purposes.
   */
  public suspend fun clear(vararg prefs: Preference<*, *>)
}

/**
 * It is expected that clients will implement this class and have val member properties
 * provided by [preference], [optPreference], [asTypePref], or [optAsTypePref] functions. The
 * [enumByNamePref] and [optEnumByNamePref] can also be used as examples of how to support
 * additional types.
 */
public open class BasePreferenceStore<T : PreferenceStore<T>>(
  private val storage: Storage
) : PreferenceStore<T> {
  private val prefSet = mutableSetOf<StorePref<*, *>>()

  internal val prefsFlow: Flow<Preferences>
    get() = storage.data

  @Suppress("UNCHECKED_CAST")
  /**
   * Why emit a [StoreHolder] instead of the [PreferenceStore] itself - to guarantee each
   * emission is not equal. We cannot control the equals methods of subclasses, so we'll wrap it
   * into another class where we can control equals.
   */
  override val updateFlow: MutableStateFlow<StoreHolder<T>> by lazy {
    MutableStateFlow(StoreHolder(this as T))
  }

  internal fun <S, A> getPreferenceValue(pref: StorePref<S, A>): A =
    pref.storedToActual(storage[pref.key])

  @Suppress("UNCHECKED_CAST")
  override suspend fun edit(block: suspend T.(MutablePreferenceStore) -> Unit) {
    // Don't copy prefSet. Due to how delegated properties behave a particular preference
    // may not exist in the set until it's referenced inside the block()
    storage.edit { (this as T).block(MutableStore(prefSet, it)) }
    updateFlow.value = StoreHolder(this as T)
  }

  override suspend fun clear(vararg prefs: Preference<*, *>) {
    if (prefs.isEmpty()) storage.clear() else storage.edit { mutable ->
      require(prefSet.containsAll(prefs.toList())) { "One of ${prefs.keys()} not in this store" }
      prefs.forEach { mutable.remove(it.key) }
    }
  }

  internal fun <S, A> register(pref: StorePref<S, A>): StorePref<S, A> =
    pref.apply { require(prefSet.add(pref)) { "Already contains key ${pref.key}" } }

  protected inline fun <reified S : Any> preference(
    default: S,
    customName: String? = null,
    noinline sanitize: Sanitize<S> = null
  ): StorePref<S, S> = UnmappedPreference(S::class, default, sanitize, this, customName)

  protected inline fun <reified S> optPreference(
    default: S? = null,
    customName: String? = null,
    noinline sanitize: Sanitize<S?> = null
  ): StorePref<S?, S?> = UnmappedPreference(S::class, default, sanitize, this, customName)

  protected inline fun <reified S : Any, A : Any> asTypePref(
    default: A,
    noinline maker: (S) -> A,
    noinline serialize: (A) -> S,
    customName: String? = null,
    noinline sanitize: Sanitize<A> = null
  ): StorePref<S, A> =
    MappedPreference(S::class, default, sanitize, this, maker, serialize, customName)

  protected inline fun <reified S, A> optAsTypePref(
    default: A?,
    noinline maker: (S?) -> A?,
    noinline serialize: (A?) -> S?,
    customName: String? = null,
    noinline sanitize: Sanitize<A?> = null
  ): StorePref<S?, A?> =
    MappedPreference(S::class, default, sanitize, this, maker, serialize, customName)

  protected fun <S : Enum<S>> enumByNamePref(
    default: S,
    customName: String? = null,
    sanitize: Sanitize<S> = null
  ): StorePref<String, S> = asTypePref(
    default,
    { default.javaClass.reifyEnum(it, default) },
    { it.name },
    customName,
    sanitize
  )

  protected inline fun <reified S : Enum<S>> optEnumByNamePref(
    default: S? = null,
    customName: String? = null,
    noinline sanitize: Sanitize<S?> = null
  ): StorePref<String?, S?> = optAsTypePref(
    default,
    { name -> S::class.java.enumConstants?.firstOrNull { it.name == name } },
    { it?.name },
    customName,
    sanitize
  )

  protected fun <S : Enum<S>> enumSetByNamePref(
    default: Set<S>,
    customName: String? = null,
    sanitize: Sanitize<Set<S>> = null
  ): StorePref<Set<String>, Set<S>> = asTypePref(
    requireNotEmpty(default) { "Default set must have at least 1 item" },
    { set ->
      if (set.isEmpty()) default
      else set.mapTo(mutableSetOf()) { default.first()::class.java.reifyEnum(it, default.first()) }
    },
    { set -> set.mapTo(mutableSetOf()) { it.name } },
    customName,
    sanitize
  )

  override fun toString(): String = storage.toString()

  @Suppress("EqualsAlwaysReturnsTrueOrFalse")
  override fun equals(other: Any?): Boolean = false
  override fun hashCode(): Int = javaClass.hashCode()
}

private class MutableStore(
  private val prefSet: Set<StorePref<*, *>>,
  private val prefs: MutablePreferences
) : MutablePreferenceStore {
  override fun <S, A> set(preference: StorePref<S, A>, value: A) {
    require(prefSet.contains(preference)) { "Preference ${preference.key} not in this store" }
    value?.let { prefs[preference.key] = preference.actualToStored(it) }
      ?: prefs.remove(preference.key)
  }

  override fun <S, A> clear(preference: StorePref<S, A>) {
    prefs.remove(preference.key)
  }

  override fun toString(): String = prefs.asMap().toString()
}

public fun <T> prefKey(name: String, theClass: KClass<*>): Preferences.Key<T> {
  @Suppress("UNCHECKED_CAST")
  return when (theClass) {
    Int::class -> intPreferencesKey(name)
    String::class -> stringPreferencesKey(name)
    Boolean::class -> booleanPreferencesKey(name)
    Float::class -> floatPreferencesKey(name)
    Long::class -> longPreferencesKey(name)
    Double::class -> doublePreferencesKey(name)
    Set::class -> stringPreferencesKey(name)
    else -> throw IllegalArgumentException("Type not supported: ${theClass.simpleName}")
  } as Preferences.Key<T>
}

private fun <T : Enum<T>> Class<out T>.reifyEnum(name: String?, default: T): T =
  enumConstants.firstOrNull { name == it.name } ?: default

private fun Array<out Preference<*, *>>.keys(): String = buildString {
  append('[')
  forEachIndexed { index: Int, preference: Preference<*, *> ->
    if (index > 0) append(", ")
    append(preference.key)
  }
  append(']')
}

private fun <T : Any> requireNotEmpty(value: Set<T>, lazyMessage: () -> Any): Set<T> {
  return if (value.isEmpty()) throw IllegalArgumentException(lazyMessage().toString()) else value
}
