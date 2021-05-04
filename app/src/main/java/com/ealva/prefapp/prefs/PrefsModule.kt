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

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

object PrefsModule {
  val koinModule = module {
    single(named("AppPrefs")) {
      AppPrefsSingleton(
        AppPrefs.Companion::make,
        androidContext(),
        "AppPrefs"
      )
    }
    single(named("SimplePrefs")) {
      SimplePrefsSingleton(
        maker = SimplePrefs.Companion::make,
        context = androidContext(),
        fileName = "SimplePrefs"
      )
    }
  }
}
