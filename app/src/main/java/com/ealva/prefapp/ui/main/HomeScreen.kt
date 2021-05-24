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

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.ealva.prefapp.ui.AppColors

@Composable
fun HomeScreen(appSettings: () -> Unit) {
  Scaffold(
    topBar = { TopAppBar(title = { Text(text = "Compose Preference Store") }) },
    bottomBar = {
      BottomAppBar(
        backgroundColor = AppColors.surface,
        contentColor = contentColorFor(AppColors.surface),
        cutoutShape = CircleShape
      ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
          IconButton(onClick = appSettings) {
            Icon(Icons.Rounded.Settings, contentDescription = "Settings")
          }
          Spacer(modifier = Modifier.weight(1f))
          IconButton(onClick = { /* doSomething() */ }) {
            Icon(Icons.Rounded.MoreVert, contentDescription = "More")
          }
        }
      }
    },
    content = { Text("Touch Settings bottom left...") }
  )
}
