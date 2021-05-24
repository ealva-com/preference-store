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

package com.ealva.prefapp.prefs

import com.ealva.prefstore.store.BasePreferenceStore
import com.ealva.prefstore.store.PreferenceStore
import com.ealva.prefstore.store.Storage
import com.ealva.prefstore.store.StorePref

@JvmInline
value class Millis(val value: Long) : Comparable<Millis> {
  override fun toString(): String = value.toString()

  override operator fun compareTo(other: Millis): Int = value.compareTo(other.value)
  operator fun minus(other: Millis): Millis = Millis(value - other.value)

  companion object {
    val ZERO = Millis(0)
    val TWO_SECONDS = Millis(2000)
  }
}

typealias MillisRange = ClosedRange<Millis>

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

enum class DuckAction {
  Duck,
  Pause,
  DoNothing;
}

enum class ItemType {
  Type1,
  Type2,
  Type3;
}

typealias MillisStorePref = StorePref<Long, Millis>
typealias VolumeStorePref = StorePref<Int, Volume>

@Suppress("SameParameterValue")
open class BaseAppPrefStore<T : PreferenceStore<T>>(
  storage: Storage
) : BasePreferenceStore<T>(storage) {
  protected fun millisPref(
    default: Millis,
    customName: String? = null,
    sanitize: ((Millis) -> Millis)? = null,
  ): MillisStorePref = asTypePref(default, ::Millis, { it.value }, customName, sanitize)

  protected fun volumePref(
    default: Volume,
    customName: String? = null,
    sanitize: ((Volume) -> Volume)? = null,
  ): VolumeStorePref = asTypePref(default, ::Volume, { it.value }, customName, sanitize)
}
