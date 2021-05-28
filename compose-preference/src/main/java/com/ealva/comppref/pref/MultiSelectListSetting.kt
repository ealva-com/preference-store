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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
public fun <A : Any> MultiSelectListSetting(item: MultiSelectListSettingItem<A>) {
  val scope = rememberCoroutineScope()
  val showDialog = remember { mutableStateOf(false) }
  val closeDialog = { showDialog.value = false }
  val selectedKeys = item.selectedKeys
  val dialogItems = item.dialogItems
  val summary = selectedKeys.joinToString(separator = ", ", limit = 3)
  val isEnabled = LocalGroupEnabledStatus.current && item.enabled

  Setting(
    title = item.title,
    summary = summary,
    singleLineTitle = item.singleLineTitle,
    onClick = { if (isEnabled) showDialog.value = true },
    icon = item.icon,
    enabled = isEnabled
  )

  if (showDialog.value) {
    AlertDialog(
      onDismissRequest = { closeDialog() },
      title = { Text(text = item.title) },
      text = {
        Column {
          dialogItems.forEach { current ->
            val isSelected = selectedKeys.contains(current.key)
            val onSelectionChanged = {
              val result = when (!isSelected) {
                true -> selectedKeys + current.key
                false -> selectedKeys - current.key
              }
              if (result.isNotEmpty()) {
                scope.launch {
                  item.preference.set(
                    result.mapTo(mutableSetOf()) { requireNotNull(dialogItems[it]) }
                  )
                }
              }
            }
            Row(
              Modifier
                .fillMaxWidth()
                .selectable(
                  selected = isSelected,
                  onClick = { onSelectionChanged() }
                )
                .padding(16.dp)
            ) {
              Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectionChanged() }
              )
              Text(
                text = current.key,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(start = 16.dp)
              )
            }
          }
        }
      },
      confirmButton = {
        TextButton(
          onClick = { closeDialog() },
          colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.secondary),
        ) {
          Text(text = "Select")
        }
      }
    )
  }
}
