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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalMaterialApi::class)
@ExperimentalCoroutinesApi
@Composable
public fun GroupSetting(item: GroupSettingItem, makers: SettingMakers) {
  Text(
    text = item.title,
    style = MaterialTheme.typography.subtitle2,
    modifier = Modifier.padding(start = 16.dp, end = 16.dp)
  )
  Surface(
    color = MaterialTheme.colors.surface,
    shape = RoundedCornerShape(8.dp),
    elevation = 4.dp,
    modifier = Modifier.padding(8.dp)
  ) {
    CompositionLocalProvider(GroupEnabledStatus provides item.enabled) {
      Column {
        item.content.forEach { groupItem ->
          makers.Make(groupItem)
//          Divider(startIndent = if (groupItem.icon != null) 40.dp else 16.dp)
          Divider(startIndent = 16.dp)
        }
      }
    }
  }
}
