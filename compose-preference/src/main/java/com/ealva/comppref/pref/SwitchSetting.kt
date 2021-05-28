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

package com.ealva.comppref.pref

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalCoroutinesApi
@Composable
public fun SwitchSetting(item: SwitchSettingItem) {
  val scope = rememberCoroutineScope()
  val onClicked: (Boolean) -> Unit = { scope.launch { item.preference.set(it) } }
  val isEnabled = LocalGroupEnabledStatus.current && item.enabled

  Setting(
    title = item.title,
    summary = item.summary,
    singleLineTitle = item.singleLineTitle,
    icon = item.icon,
    enabled = isEnabled,
    onClick = { onClicked(!item.value) }
  ) {
    Switch(checked = item.value, onCheckedChange = { onClicked(it) }, enabled = isEnabled)
  }
}
