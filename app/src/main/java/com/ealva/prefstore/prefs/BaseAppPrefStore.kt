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

package com.ealva.prefstore.prefs

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ealva.prefstore.store.BasePreferenceStore
import com.ealva.prefstore.store.PreferenceStore
import com.ealva.prefstore.store.StorePref
import com.ealva.prefstore.store.UnmappedPref
import kotlinx.coroutines.flow.StateFlow

@JvmInline
value class Millis(val value: Long) : Comparable<Millis> {
  override fun toString(): String = value.toString()

  override operator fun compareTo(other: Millis): Int = value.compareTo(other.value)

  companion object {
    val ZERO = Millis(0)
  }
}

@JvmInline
value class Volume(val value: Int) : Comparable<Volume> {
  override fun toString(): String = value.toString()

  override fun compareTo(other: Volume): Int = value.compareTo(other.value)

  companion object {
    val OFF = Volume(0)
    val HALF = Volume(50)
    val FULL = Volume(100)
  }
}

typealias VolumeRange = ClosedRange<Volume>

enum class DuckAction  {
  Duck,
  Pause,
  DoNothing;
}

typealias MillisStorePref = StorePref<Long, Millis>
typealias VolumeStorePref = StorePref<Int, Volume>

@Suppress("SameParameterValue")
open class BaseAppPrefStore<T : PreferenceStore<T>>(
  dataStore: DataStore<Preferences>,
  stateFlow: StateFlow<Preferences>
) : BasePreferenceStore<T>(dataStore, stateFlow) {
  protected fun millisPref(
    name: String,
    default: Millis,
    sanitize: ((Millis) -> Millis)? = null
  ): MillisStorePref = makePreference(name, default, ::Millis, { it.value }, sanitize)

  protected fun volumePref(
    name: String,
    default: Volume,
    sanitize: ((Volume) -> Volume)? = null
  ): VolumeStorePref = makePreference(name, default, ::Volume, { it.value }, sanitize)
}
