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

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalCoroutinesApi
@Composable
public fun SettingsScreen(
  items: List<SettingItem>,
  modifier: Modifier = Modifier,
  makers: SettingMakers = SettingMakers.DEFAULT
) {
  LazyColumn(modifier = modifier) {
    items(items = items) { item ->
      makers.Make(item = item)
    }
  }
}

public interface SettingMakers {
  @OptIn(ExperimentalCoroutinesApi::class)
  @Composable
  public fun Make(item: SettingItem)

  public companion object {
    @ExperimentalMaterialApi
    public val DEFAULT: SettingMakers = DefaultSettingMakers()
  }
}

public open class DefaultSettingMakers : SettingMakers {
  @OptIn(ExperimentalCoroutinesApi::class, ExperimentalMaterialApi::class)
  @Composable
  override fun Make(item: SettingItem) {
    when (item) {
      is SwitchSettingItem -> SwitchSetting(item)
      is CheckboxSettingItem -> CheckboxSetting(item)
      is ListSettingItem<*, *> -> ListSetting(item)
      is SliderSettingItem<*, *> -> SliderSetting(item)
      is GroupSettingItem -> GroupSetting(item, this)
      is CallbackSettingItem -> CallbackSetting(item)
      is ButtonSettingItem -> ButtonSetting(item)
      is MultiSelectListSettingItem<*> -> MultiSelectListSetting(item)
      else -> println("No handler for $item")
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Composable
  private fun CheckboxSetting(item: CheckboxSettingItem) {
    CheckboxSetting(item) { checked, clicked, enabled -> Checkbox(checked, clicked, enabled) }
  }

  @Composable
  protected open fun Checkbox(
    isChecked: Boolean,
    onClicked: ((Boolean) -> Unit)?,
    isEnabled: Boolean
  ) {
    Checkbox(
      checked = isChecked,
      onCheckedChange = onClicked,
      enabled = isEnabled,
      colors = CheckboxDefaults.colors()
    )
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Composable
  protected open fun SwitchSetting(item: SwitchSettingItem) {
    SwitchSetting(item) { checked, clicked, enabled -> Switch(checked, clicked, enabled) }
  }

  @Composable
  protected open fun Switch(
    isChecked: Boolean,
    onClicked: ((Boolean) -> Unit)?,
    isEnabled: Boolean
  ) {
    Switch(
      checked = isChecked,
      onCheckedChange = onClicked,
      enabled = isEnabled,
      colors = SwitchDefaults.colors(uncheckedThumbColor = Color.White)
    )
  }
}
