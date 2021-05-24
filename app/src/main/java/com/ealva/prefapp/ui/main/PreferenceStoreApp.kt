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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ealva.prefapp.ui.main.Destinations.AppSettings
import com.ealva.prefapp.ui.main.Destinations.SimpleSettings
import com.ealva.prefapp.ui.main.Destinations.Home

@Composable
fun PreferenceStoreApp() {
  val navController = rememberNavController()
  val actions = remember(navController) { Actions(navController) }
  NavHost(navController, startDestination = Home) {
    composable(Home) {
      HomeScreen(actions.appSettings)
    }
    composable(AppSettings) {
      AppSettings(actions, actions.navigateUp, it.lifecycle)
    }
    composable(SimpleSettings) {
      SimpleSettings(actions.navigateUp, it.lifecycle)
    }
  }
}
