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

package com.ealva.prefapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ealva.prefapp.prefs.AnEnum
import com.ealva.prefapp.prefs.AppPrefsSingleton
import com.ealva.prefapp.prefs.DuckAction
import com.ealva.prefapp.prefs.Millis
import com.ealva.prefapp.prefs.SimplePrefs
import com.ealva.prefapp.prefs.SimplePrefsSingleton
import com.ealva.prefapp.prefs.Volume
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
@Suppress("MagicNumber")
class MainActivity : AppCompatActivity() {
  private val simplePrefsSingleton: SimplePrefsSingleton by inject(qualifier = named("SimplePrefs"))
  private val prefsSingleton: AppPrefsSingleton by inject(qualifier = named("AppPrefs"))

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Must be inside a coroutine to make changes to preferences
    lifecycleScope.launch {
      // Singleton has an invoke function we can use to access the instance
      prefsSingleton {
        firstRun(false)  // set individual preference

        // If setting more than one pref, use edit to commit all changes as a block
        edit {
          it[duckAction] = DuckAction.Duck
          it[duckVolume] = Volume(30)
          it[lastScanTime] = Millis(System.currentTimeMillis())
        }
      }

      simplePrefsSingleton {
        this.setSomePrefs()
        this.startChangingInt()
        this.watchSimplePrefsInt()
      }
    }
  }

  private suspend fun SimplePrefs.setSomePrefs() {
    anEnum(AnEnum.First) // commits the single preference anEnum
    someBool(false)
    // commit preferences as a single unit
    edit {
      it[someBool] = true
      it[someInt] = 100
      it[anEnum] = AnEnum.Another
    }
    require(anEnum() == AnEnum.Another) // invoke the preference to get it's value
    require(someBool())
  }

  private fun SimplePrefs.startChangingInt() {
    lifecycleScope.launch {
      while (isActive) {
        delay(2000)
        someInt(someInt() + 10)
      }
    }
  }

  private suspend fun SimplePrefs.watchSimplePrefsInt() {
    someInt.asFlow().collect {
      println("SimplePrefs.someInt changed: $it")
    }
  }
}
