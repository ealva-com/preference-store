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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalCoroutinesApi
@Composable
public fun SettingsScreen(
  items: List<SettingItem>,
  makers: SettingMakers = SettingMakers.DEFAULT
) {
  LazyColumn {
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

@ExperimentalMaterialApi
private class DefaultSettingMakers : SettingMakers {
  @OptIn(ExperimentalCoroutinesApi::class)
  @Composable
  override fun Make(item: SettingItem) {
    when (item) {
      is SwitchSettingItem -> SwitchSetting(item)
      is CheckboxSettingItem -> CheckboxSetting(item)
      is ListSettingItem<*> -> ListSetting(item)
      is SliderSettingItem<*, *> -> SliderSetting(item)
      is GroupSettingItem -> GroupSetting(item, this)
      is CallbackSettingItem -> CallbackSetting(item)
      is ButtonSettingItem -> ButtonSetting(item)
      is MultiSelectListSettingItem<*> -> MultiSelectListSetting(item)
      else -> println("No handler for $item")
    }
  }
}
