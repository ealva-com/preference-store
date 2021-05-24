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

import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.ealva.prefstore.store.invoke
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
public fun <S, A : Comparable<A>> SliderSetting(item: SliderSettingItem<S, A>) {
  val scope = rememberCoroutineScope()
  val currentValue = remember { mutableStateOf(item.typeToFloat(item.value)) }
  val isEnabled = GroupEnabledStatus.current && item.enabled

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
        }
      )
    },
    singleLineTitle = item.singleLineTitle,
    icon = item.icon,
    enabled = isEnabled
  )
}

private const val MARGIN_AMOUNT: Int = -8
private val NEGATIVE_MARGIN_ADJUSTMENT_UP: Dp = MARGIN_AMOUNT.dp

@Composable
private fun SliderSettingSummary(
  item: SliderSettingItem<*, *>,
  isEnabled: Boolean,
  sliderValue: Float,
  valueRange: ClosedFloatingPointRange<Float>,
  onValueChanged: (Float) -> Unit,
  onValueChangeEnd: () -> Unit,
) {
  ConstraintLayout {
    val (summary, value, slider) = createRefs()
    val textModifier = if (isEnabled) Modifier else Modifier.alpha(ContentAlpha.disabled)
    Text(
      text = item.summary,
      modifier = textModifier.constrainAs(summary) {
        top.linkTo(parent.top)
        start.linkTo(parent.start)
      }
    )
    Text(
      text = item.valueRepresentation(sliderValue),
      textAlign = TextAlign.End,
      modifier = textModifier.constrainAs(value) {
        top.linkTo(parent.top)
        end.linkTo(parent.end, 8.dp)
      }
    )
    Slider(
      value = sliderValue,
      onValueChange = { if (isEnabled) onValueChanged(it) },
      valueRange = valueRange,
      steps = item.steps,
      onValueChangeFinished = onValueChangeEnd,
      modifier = Modifier.constrainAs(slider) {
        top.linkTo(summary.bottom, NEGATIVE_MARGIN_ADJUSTMENT_UP)
        start.linkTo(parent.start)
        end.linkTo(parent.end)
      },
      enabled = isEnabled
    )
  }
}
