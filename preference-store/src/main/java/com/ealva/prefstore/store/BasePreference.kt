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

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * BasePreference provides necessary functionality for [PreferenceStore.Preference] implementation
 * except for mapping from stored value to actual and vice versa.
 *
 * All preferences in a PreferenceStore need to be unique by Key, so equals and hashCode are
 * implemented in this class and accept subclasses. Subclasses need not implement these methods.
 */
public abstract class BasePreference<S, A, T : PreferenceStore<T>>(
  private val theClass: KClass<*>,
  override val default: A,
  override val sanitize: Sanitize<A>,
  private val store: BasePreferenceStore<T>,
  private val customName: String? = null,
) : StorePref<S, A> {
  private lateinit var name: String
  private val keyName: String
    get() {
      if (!::name.isInitialized) {
        throw IllegalStateException("Preference must be a delegated property. Use 'by' syntax.")
      }
      return customName ?: name
    }

  override val key: Preferences.Key<S> by lazy { prefKey(keyName, theClass) }

  override fun invoke(): A = store.getPreferenceValue(this)

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

  override fun getValue(
    thisRef: PreferenceStore<*>,
    property: KProperty<*>
  ): StorePref<S, A> = apply {
    if (!::name.isInitialized) {
      name = property.name
      store.register(this)
    }
  }

  /** To be equal requires the same key and in the same store */
  override fun equals(other: Any?): Boolean = when {
    this === other -> true
    other !is BasePreference<*, *, *> -> false
    other.store !== store -> false
    else -> key == other.key
  }

  override fun hashCode(): Int {
    var result = store.hashCode()
    result = 31 * result + key.hashCode()
    return result
  }

  override fun toString(): String = "$key=${invoke()}"
}

public suspend inline operator fun <S, A> StorePref<S, A>.invoke(value: A): Unit =
  set(value)

public class UnmappedPreference<S, T : PreferenceStore<T>>(
  theClass: KClass<*>,
  default: S,
  sanitize: Sanitize<S>,
  store: BasePreferenceStore<T>,
  customName: String? = null,
) : BasePreference<S, S, T>(theClass, default, sanitize, store, customName) {
  override fun doStoredToActual(stored: S): S = stored
  override fun doActualToStored(actual: S): S = actual
}

public typealias BoolPref = PreferenceStore.Preference<Boolean, Boolean>
public typealias OptBoolPref = PreferenceStore.Preference<Boolean?, Boolean?>
public typealias IntPref = PreferenceStore.Preference<Int, Int>
public typealias OptIntPref = PreferenceStore.Preference<Int?, Int?>
public typealias LongPref = PreferenceStore.Preference<Long, Long>
public typealias OptLongPref = PreferenceStore.Preference<Long?, Long?>
public typealias FloatPref = PreferenceStore.Preference<Float, Float>
public typealias OptFloatPref = PreferenceStore.Preference<Float?, Float?>
public typealias DoublePref = PreferenceStore.Preference<Double, Double>
public typealias OptDoublePref = PreferenceStore.Preference<Double?, Double?>
public typealias StringPref = PreferenceStore.Preference<String, String>
public typealias OptStringPref = PreferenceStore.Preference<String?, String?>

public class MappedPreference<S, A, T : PreferenceStore<T>>(
  theClass: KClass<*>,
  default: A,
  sanitize: Sanitize<A>,
  store: BasePreferenceStore<T>,
  private val maker: (S) -> A,
  private val serialize: (A) -> S,
  customName: String?
) : BasePreference<S, A, T>(theClass, default, sanitize, store, customName) {
  override fun doStoredToActual(stored: S): A = maker(stored)
  override fun doActualToStored(actual: A): S = serialize(actual)
}
