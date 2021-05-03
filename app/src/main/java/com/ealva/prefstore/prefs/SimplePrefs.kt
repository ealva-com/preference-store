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
import com.ealva.prefstore.store.PreferenceStoreSingleton
import com.ealva.prefstore.store.StorePref
import com.ealva.prefstore.store.UnmappedPref
import kotlinx.coroutines.flow.StateFlow

enum class AnEnum {
  First,
  Another;
}

/** SimplePrefsSingleton(SimplePrefs.Companion::make, androidContext(), fileName) */
typealias SimplePrefsSingleton = PreferenceStoreSingleton<SimplePrefs>

interface SimplePrefs : PreferenceStore<SimplePrefs> {
  val someBool: UnmappedPref<Boolean>
  val someInt: UnmappedPref<Int>
  val anEnum: StorePref<String, AnEnum>
  companion object {
    fun make(dataStore: DataStore<Preferences>, stateFlow: StateFlow<Preferences>): SimplePrefs =
      SimplePrefsImpl(dataStore, stateFlow)
  }
}

private class SimplePrefsImpl(
  dataStore: DataStore<Preferences>,
  stateFlow: StateFlow<Preferences>
) : BasePreferenceStore<SimplePrefs>(dataStore, stateFlow), SimplePrefs {
  override val someBool = boolPreference("some_bool", false)
  override val someInt = intPreference("some_int", 100)
  override val anEnum = enumByNamePreference("an_enum", AnEnum.First)
}
