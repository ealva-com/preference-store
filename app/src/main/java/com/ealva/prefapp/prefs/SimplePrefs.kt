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
import com.ealva.prefstore.store.BoolPref
import com.ealva.prefstore.store.IntPref
import com.ealva.prefstore.store.PreferenceStore
import com.ealva.prefstore.store.PreferenceStoreSingleton
import com.ealva.prefstore.store.Storage
import com.ealva.prefstore.store.StorePref

enum class AnEnum {
  First,
  Another;
}

/** SimplePrefsSingleton(SimplePrefs.Companion::make, androidContext(), fileName) */
typealias SimplePrefsSingleton = PreferenceStoreSingleton<SimplePrefs>

interface SimplePrefs : PreferenceStore<SimplePrefs> {
  val someBool: BoolPref
  val someInt: IntPref
  val anEnum: StorePref<String, AnEnum>

  companion object {
    fun make(storage: Storage): SimplePrefs = SimplePrefsImpl(storage)
  }
}

@Suppress("MagicNumber")
private class SimplePrefsImpl(
  storage: Storage
) : BasePreferenceStore<SimplePrefs>(storage), SimplePrefs {
  override val someBool by preference(false)
  override val someInt by preference(100)
  override val anEnum by enumByNamePref(AnEnum.First)
}
