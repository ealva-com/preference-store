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
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ealva.prefstore.store.PreferenceStore.Preference
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

public typealias StorePref<S, A> = Preference<S, A>
public typealias UnmappedPref<T> = Preference<T, T>
public typealias Sanitize<T> = ((T) -> T)? // optional conversion function

public interface MutablePreferenceStore {
  public operator fun <S : Any, A : Any> set(preference: StorePref<S, A>, value: A)
}

/**
 * PreferenceStore wraps a DataStore<Preference> providing a higher level of abstraction of a
 * [Preference], getting and setting values, flows of values for a [Preference], and mapping of
 * types used by a client and the type stored in the [DataStore].
 *
 * PreferenceStore is parameterized with the subclass that contains the [Preference] values
 */
public interface PreferenceStore<out T : PreferenceStore<T>> {

  public interface Preference<Stored : Any, Actual : Any> {
    /** The key used to store and retrieve the preference value */
    public val key: Preferences.Key<Stored>

    /** Will be returned if the preference has never been set and used in [resetToDefaultExcept] */
    public val default: Actual

    /**
     * Sanitize is an optional function that accepts/rejects/maps a value. [actualToStored]
     * will call this function, if not null, before any conversion.
     */
    public val sanitize: Sanitize<Actual>

    /** Invoke a preference to get its value. */
    public operator fun invoke(): Actual

    /**
     * Set the Preference to [value]. An edit/commit is performed. If modifying several preferences,
     * use the [edit] function to keep all modification within a single commit.
     */
    public suspend fun set(value: Actual)

    /**
     * If the stored and actual types differ, this function converts the stored type to the actual
     * type. If stored is null, the [default] is returned. As an example, an enum type may
     * store it's name and this function would convert the name back to the enum instance.
     */
    public fun storedToActual(stored: Stored?): Actual

    /**
     * If the actual and stored types differ, this function converts the actual type to the stored
     * type. For example, an enum type may store it's name. If [sanitize] is not null, [actual] will
     * be sanitized before conversion to [Stored] type.
     */
    public fun actualToStored(actual: Actual): Stored

    /**
     * A [Flow] of values from this preference. As values are committed they will be emitted from
     * this flow
     */
    public fun asFlow(): Flow<Actual>

    /** Store [default] into [mutablePreferences] under [key] */
    public fun storeDefault(mutablePreferences: MutablePreferences)
  }

  /**
   * Call the [block] function with a [MutablePreferenceStore] where multiple preference values may
   * be committed as one. This is more performant than calling individual [Preference.set]s as there
   * will be one commit (one write, one emit, etc.) when [block] exits.
   */
  public suspend fun edit(block: T.(MutablePreferenceStore) -> Unit)

  /**
   * Resets all preferences except those indicated by [predicate]. All preferences in this store are
   * passed to [predicate] and if [predicate] returns true the preference is excluded. If
   * [predicate] returns false, the default, then the preference default value is stored.
   */
  public suspend fun resetToDefaultExcept(predicate: (StorePref<*, *>) -> Boolean = { false })

  /**
   * Retrieve a map of all key preference pairs. The returned map is unmodifiable, and attempts
   * to mutate it will throw runtime exceptions.
   */
  public fun asMap(): Map<Preferences.Key<*>, Any>
}

public suspend inline fun <T : PreferenceStore<T>> T.resetAllToDefault(): Unit =
  resetToDefaultExcept()

public suspend inline operator fun <S : Any, A : Any> StorePref<S, A>.invoke(value: A): Unit =
  set(value)

/**
 * BasePreference provides necessary functionality for [PreferenceStore.Preference] implementation
 * except for mapping from stored value to actual and vice versa.
 *
 * All preferences in a PreferenceStore need to be unique by Key, so equals and hashCode are
 * implemented in this class and accept subclasses. Subclasses need not implement these methods.
 */
public abstract class BasePreference<S : Any, A : Any>(
  override val key: Preferences.Key<S>,
  override val default: A,
  override val sanitize: Sanitize<A>,
  private val store: BasePreferenceStore<*>
) : StorePref<S, A> {
  override fun invoke(): A = store.latest(this)

  override suspend fun set(value: A) {
    store.edit { it[this@BasePreference] = value }
  }

  /** If [stored] is null [default] is returned, else it is converted to actual and returned */
  final override fun storedToActual(stored: S?): A =
    stored?.let { doStoredToActual(stored) } ?: default

  protected abstract fun doStoredToActual(stored: S): A

  /** Sanitize value if necessary then call [doActualToStored] */
  final override fun actualToStored(actual: A): S =
    sanitize?.let { doActualToStored(it(actual)) } ?: doActualToStored(actual)

  protected abstract fun doActualToStored(actual: A): S

  override fun asFlow(): Flow<A> = store.data
    .catch { ex -> if (ex is IOException) emit(emptyPreferences()) else throw ex }
    .map { preferences -> preferences[key] }
    .map { stored -> storedToActual(stored) }

  override fun storeDefault(mutablePreferences: MutablePreferences) {
    mutablePreferences[key] = actualToStored(default)
  }

  override fun equals(other: Any?): Boolean = when {
    this === other -> true
    other !is BasePreference<*, *> -> false
    else -> key == other.key
  }

  override fun hashCode(): Int = key.hashCode()

  override fun toString(): String = "$key=${invoke()}"
}

/**
 * [Unmapped] is for all the DataStore<Preference> default types. Stored and Actual
 * require no mapping as they are either String or primitive types
 */
private class Unmapped<T : Any>(
  key: Preferences.Key<T>,
  defaultValue: T,
  sanitize: Sanitize<T> = null,
  store: BasePreferenceStore<*>,
) : BasePreference<T, T>(key, defaultValue, sanitize, store) {
  override fun doStoredToActual(stored: T): T = stored
  override fun doActualToStored(actual: T): T = actual
}

/**
 * It is expected that clients will implement this class and provide val members, constructed with
 * one of the various protected typePreference functions. If more sophisticated types are
 * required, the [makePreference] function can be called to build the proper Preference
 * implementation.
 */
public open class BasePreferenceStore<T : PreferenceStore<T>>(
  private val dataStore: DataStore<Preferences>,
  private var lastPreferences: Preferences
) : PreferenceStore<T> {
  private val prefSet = mutableSetOf<StorePref<*, *>>()

  public val data: Flow<Preferences>
    get() = dataStore.data

  public fun <S : Any, A : Any> latest(pref: StorePref<S, A>): A {
    return pref.storedToActual(lastPreferences[pref.key])
  }

  override suspend fun edit(block: T.(MutablePreferenceStore) -> Unit) {
    lastPreferences = dataStore.edit { mutablePreferences ->
      @Suppress("UNCHECKED_CAST") (this as T).block(MutableStore(prefSet, mutablePreferences))
    }
  }

  override suspend fun resetToDefaultExcept(predicate: (StorePref<*, *>) -> Boolean) {
    lastPreferences = dataStore.edit { mutablePreferences ->
      prefSet
        .filterNot(predicate)
        .forEach { storePref -> storePref.storeDefault(mutablePreferences) }
    }
  }

  final override fun asMap(): Map<Preferences.Key<*>, Any> = lastPreferences.asMap()

  private inline fun <reified T : Any> makePreference(
    name: String,
    default: T,
    noinline sanitize: Sanitize<T>
  ): UnmappedPref<T> = register(Unmapped(prefKey(name), default, sanitize, this))

  protected fun <S : Any, A : Any> register(pref: StorePref<S, A>): StorePref<S, A> =
    pref.apply { require(prefSet.add(pref)) { "Already contains key ${pref.key}" } }

  protected fun intPreference(
    name: String,
    default: Int,
    sanitize: Sanitize<Int> = null
  ): UnmappedPref<Int> = makePreference(name, default, sanitize)

  protected fun stringPreference(
    name: String,
    default: String,
    sanitize: Sanitize<String> = null
  ): UnmappedPref<String> = makePreference(name, default, sanitize)

  protected fun boolPreference(
    name: String,
    default: Boolean,
    sanitize: Sanitize<Boolean> = null
  ): UnmappedPref<Boolean> =
    makePreference(name, default, sanitize)

  protected fun floatPreference(
    name: String,
    default: Float,
    sanitize: Sanitize<Float> = null
  ): UnmappedPref<Float> = makePreference(name, default, sanitize)

  protected fun longPreference(
    name: String,
    default: Long,
    sanitize: Sanitize<Long> = null
  ): UnmappedPref<Long> = makePreference(name, default, sanitize)

  protected fun doublePreference(
    name: String,
    default: Double,
    sanitize: Sanitize<Double> = null
  ): UnmappedPref<Double> = makePreference(name, default, sanitize)

  protected fun <T : Enum<T>> enumByNamePreference(name: String, default: T): StorePref<String, T> =
    makePreference(name, default, { default.javaClass.reifyEnum(it, default) }, { it.name }, null)

  protected inline fun <reified S : Any, A : Any> makePreference(
    name: String,
    default: A,
    crossinline maker: (S) -> A,
    crossinline serialize: (A) -> S,
    noinline sanitize: Sanitize<A> = null
  ): StorePref<S, A> = register(
    object : BasePreference<S, A>(prefKey(name), default, sanitize, this) {
      override fun doStoredToActual(stored: S): A = maker(stored)
      override fun doActualToStored(actual: A): S = serialize(actual)
    }
  )

  override fun toString(): String = lastPreferences.toString()
}

private class MutableStore(
  private val prefSet: Set<StorePref<*, *>>,
  private val prefs: MutablePreferences
) : MutablePreferenceStore {
  override fun <S : Any, A : Any> set(preference: StorePref<S, A>, value: A) {
    require(prefSet.contains(preference)) { "Preference ${preference.key} not in this store" }
    prefs[preference.key] = preference.actualToStored(value)
  }
}

public inline fun <reified T : Any> prefKey(name: String): Preferences.Key<T> {
  @Suppress("UNCHECKED_CAST")
  return when (T::class) {
    Int::class -> intPreferencesKey(name)
    String::class -> stringPreferencesKey(name)
    Boolean::class -> booleanPreferencesKey(name)
    Float::class -> floatPreferencesKey(name)
    Long::class -> longPreferencesKey(name)
    Double::class -> doublePreferencesKey(name)
    else -> throw IllegalArgumentException("Type not supported: ${T::class.java}")
  } as Preferences.Key<T>
}

private fun <T : Enum<T>> Class<T>.reifyEnum(name: String?, default: T): T =
  enumConstants.firstOrNull { name == it.name } ?: default
