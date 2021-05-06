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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ealva.prefapp.prefs.AppPrefs.Companion.DUCK_VOLUME_RANGE
import com.ealva.prefstore.store.BoolPref
import com.ealva.prefstore.store.PreferenceStore
import com.ealva.prefstore.store.PreferenceStoreSingleton
import com.ealva.prefstore.store.Storage
import com.ealva.prefstore.store.StorePref

/** To build:
 * ```AppPrefsSingleton(AppPrefs.Companion::make, androidContext(), fileName)```
 */
typealias AppPrefsSingleton = PreferenceStoreSingleton<AppPrefs>

/**
 * Define an interface for our preferences to facilitate injecting test stubs, managing
 * dependencies, and to hide implementation details. Clients only need know about this interface.
 */
interface AppPrefs : PreferenceStore<AppPrefs> {
  val firstRun: BoolPref // stores and provides Boolean
  val lastScanTime: StorePref<Long, Millis> // stores Long, provides value class Millis
  val duckAction: StorePref<String, DuckAction> // stored String, provides enum DuckAction
  val duckVolume: StorePref<Int, Volume> // Stores Int, provides value class Volume

  companion object {
    val DUCK_VOLUME_RANGE: VolumeRange = Volume.OFF..Volume.FULL

    /** Construct the AppPrefs implementation */
    fun make(storage: Storage): AppPrefs = AppPrefsImpl(storage)
  }
}

/**
 * Implement a [BaseAppPrefStore], which we defined separately to include common types. We might
 * have different PreferenceStores in the app, for example 1 for the entire app, 1 for a particular
 * service, another for a difference service, etc. We would put all common preference
 * specializations in a base class.
 */
private class AppPrefsImpl(storage: Storage) : BaseAppPrefStore<AppPrefs>(storage), AppPrefs {

  override val firstRun by preference(true)
  override val lastScanTime by millisPref(Millis.ZERO)
  override val duckAction by enumByNamePref(DuckAction.Duck)

  /**
   * This preference includes a Sanitize function where it coerces the value to be within the
   * volume range. All preferences may have a Sanitize function to control what is stored (or
   * rejected as invalid)
   */
  override val duckVolume by volumePref(Volume.HALF) {
    it.coerceIn(DUCK_VOLUME_RANGE)
  }
}
