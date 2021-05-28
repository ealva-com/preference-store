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

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.ealva.comppref.pref.SettingItem
import com.ealva.comppref.pref.SettingsScreen
import com.ealva.comppref.pref.SwitchSettingItem
import com.ealva.prefapp.prefs.SimplePrefs
import com.ealva.prefapp.prefs.SimplePrefsSingleton
import com.ealva.prefapp.ui.AppColors
import com.ealva.prefstore.store.BoolPref
import com.ealva.prefstore.store.PreferenceStoreSingleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.get

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun SimpleSettings(
  navigateUp: () -> Unit,
  lifecycle: Lifecycle,
  prefsSingleton: SimplePrefsSingleton =
    get(PreferenceStoreSingleton::class.java, named("SimplePrefs"))
) {
  val scope = rememberCoroutineScope()
  val settingsState: MutableState<List<SettingItem>> = remember {
    mutableStateOf(listOf())
  }
  LaunchedEffect(settingsState) {
    scope.launch {
      prefsSingleton
        .asFlow()
        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        .collect { prefs -> settingsState.value = makeSettings(prefs) }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        backgroundColor = AppColors.primary,
        contentColor = contentColorFor(AppColors.primary),
        elevation = 0.dp,
        navigationIcon = {
          IconButton(onClick = navigateUp) {
            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
          }
        },
        title = { Text(text = "Compose Preference Store") }
      )
    },
    content = { SettingsScreen(items = settingsState.value) }
  )
}

private fun makeSettings(prefs: SimplePrefs): List<SettingItem> = listOf(
  makeFirstRunSetting(prefs.someBool)
)

private fun makeFirstRunSetting(someBool: BoolPref) = SwitchSettingItem(
  preference = someBool,
  title = "Some Bool",
  summary = "A boolean switch setting",
  singleLineTitle = true,
)
