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

package com.ealva.prefstore.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ealva.prefstore.prefs.AnEnum
import com.ealva.prefstore.prefs.AppPrefsSingleton
import com.ealva.prefstore.prefs.DuckAction
import com.ealva.prefstore.prefs.Millis
import com.ealva.prefstore.prefs.SimplePrefs
import com.ealva.prefstore.prefs.SimplePrefsSingleton
import com.ealva.prefstore.prefs.Volume
import com.ealva.prefstore.store.invoke
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

/**
 * Code here is a verbose for illustration
 */
class MainActivity : AppCompatActivity() {
  private val simplePrefsSingleton: SimplePrefsSingleton by inject(qualifier = named("SimplePrefs"))
  private val prefsSingleton: AppPrefsSingleton by inject(qualifier = named("AppPrefs"))

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Must be inside a coroutine to make changes to preferences
    lifecycleScope.launch {
      // Singleton has an invoke function we can use to access the instance
      prefsSingleton { appPrefs ->
        appPrefs.firstRun(false)  // set individual preference

        // If setting more than one pref, use edit to commit all changes as a block
        appPrefs.edit {
          it[duckAction] = DuckAction.Duck
          it[duckVolume] = Volume(30)
          it[lastScanTime] = Millis(System.currentTimeMillis())
        }
      }

      simplePrefsSingleton { simplePrefs ->
        setSomePrefs(simplePrefs)
        startChangingInt(simplePrefs)
        watchSimplePrefsInt(simplePrefs)
      }
    }
  }

  private suspend fun setSomePrefs(simplePrefs: SimplePrefs) {
    simplePrefs.anEnum(AnEnum.First) // commits the single preference anEnum
    // commit preferences as a single unit
    simplePrefs.edit {
      it[someBool] = false
      it[someInt] = 100
      it[anEnum] = AnEnum.Another
    }
    require(simplePrefs.anEnum() == AnEnum.Another)
    require(!simplePrefs.someBool())
  }

  private fun startChangingInt(simplePrefs: SimplePrefs) {
    lifecycleScope.launch {
      while (isActive) {
        delay(2000)
        simplePrefs.someInt(simplePrefs.someInt() + 10)
      }
    }
  }

  private suspend fun watchSimplePrefsInt(simplePrefs: SimplePrefs) {
    simplePrefs.someInt.asFlow().collect {
      println("SimplePrefs.someInt changed: ${simplePrefs.someInt()}")
    }
  }
}
