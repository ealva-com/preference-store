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

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ealva.prefstore.store.invoke
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
public fun <S, A : Comparable<A>> SliderSetting(item: SliderSettingItem<S, A>) {
  val scope = rememberCoroutineScope()
  val currentValue = remember { mutableStateOf(item.typeToFloat(item.value)) }
  val isEnabled = LocalGroupEnabledStatus.current && item.enabled

  Setting(
    title = item.title,
    summary = {
      SliderSettingSummary(
        item = item,
        isEnabled = isEnabled,
        sliderValue = currentValue.value,
        valueRange = item.valueRange,
        onValueChanged = { currentValue.value = it },
        onValueChangeEnd = {
          scope.launch { item.preference(item.floatToType(currentValue.value)) }
        },
        sliderModifier = Modifier
          .fillMaxWidth()
          .padding(start = if (item.iconDrawable > 0) 0.dp else 12.dp, end = 12.dp)
      )
    },
    singleLineTitle = item.singleLineTitle,
    iconDrawable = item.iconDrawable,
    enabled = isEnabled
  )
}

@SuppressLint("ModifierParameter")
@Composable
private fun SliderSettingSummary(
  item: SliderSettingItem<*, *>,
  isEnabled: Boolean,
  sliderValue: Float,
  valueRange: ClosedFloatingPointRange<Float>,
  onValueChanged: (Float) -> Unit,
  onValueChangeEnd: () -> Unit,
  sliderModifier: Modifier
) {
  val textModifier = if (isEnabled) Modifier else Modifier.alpha(ContentAlpha.disabled)
  Column(
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = item.summary,
        textAlign = TextAlign.Start,
        modifier = textModifier.weight(1F)
      )
      Text(
        text = item.valueRepresentation(sliderValue),
        textAlign = TextAlign.End,
        modifier = textModifier.padding(start = 12.dp)
      )
    }
    Slider(
      value = sliderValue,
      onValueChange = { if (isEnabled) onValueChanged(it) },
      valueRange = valueRange,
      steps = item.steps,
      onValueChangeFinished = onValueChangeEnd,
      modifier = sliderModifier,
      enabled = isEnabled
    )
  }
}
