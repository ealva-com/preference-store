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

import androidx.annotation.DrawableRes
import com.ealva.prefstore.store.StorePref

public interface SettingItem {
  public val title: String
  public val enabled: Boolean
}

public interface PreferenceSettingItem<S, A> : SettingItem {
  public val preference: StorePref<S, A>
  public val singleLineTitle: Boolean
  public val iconDrawable: Int
}

public interface BooleanSettingItem : PreferenceSettingItem<Boolean, Boolean> {
  public val summary: String
  public val value: Boolean
}

public data class SwitchSettingItem(
  override val preference: StorePref<Boolean, Boolean>,
  override val title: String,
  /** Summary displayed if switch is on or always if [offSummary] is null */
  override val summary: String,
  /** Summary to display if switch is off. If null, [summary] is displayed */
  public val offSummary: String? = null,
  override val singleLineTitle: Boolean = true,
  @DrawableRes override val iconDrawable: Int = 0,
  override val enabled: Boolean = true,
  override val value: Boolean = preference()
) : BooleanSettingItem

public data class CheckboxSettingItem(
  override val preference: StorePref<Boolean, Boolean>,
  override val title: String,
  override val summary: String,
  override val singleLineTitle: Boolean = true,
  @DrawableRes override val iconDrawable: Int = 0,
  override val enabled: Boolean = true,
  override val value: Boolean = preference()
) : BooleanSettingItem

public data class ListSettingItem<S : Any, A : Any>(
  override val preference: StorePref<S, A>,
  override val title: String,
  override val singleLineTitle: Boolean = true,
  @DrawableRes override val iconDrawable: Int = 0,
  override val enabled: Boolean = true,
  public val dialogItems: Map<String, A>,
  public val selectedKey: String = dialogItems.getKey(preference())
) : PreferenceSettingItem<S, A>

private fun <K, V> Map<K, V>.getKey(target: V): K = keys.first { target == get(it) }

public data class MultiSelectListSettingItem<A : Any>(
  override val preference: StorePref<Set<String>, Set<A>>,
  override val title: String,
  override val singleLineTitle: Boolean = true,
  @DrawableRes override val iconDrawable: Int = 0,
  override val enabled: Boolean = true,
  public val dialogItems: Map<String, A>,
  public val selectedKeys: Set<String> =
    preference().mapTo(mutableSetOf()) { dialogItems.getKey(it) }
) : PreferenceSettingItem<Set<String>, Set<A>>

public data class SliderSettingItem<S, A : Comparable<A>>(
  override val preference: StorePref<S, A>,
  override val title: String,
  public val value: A = preference(),
  override val singleLineTitle: Boolean = true,
  @DrawableRes override val iconDrawable: Int = 0,
  override val enabled: Boolean = true,
  public val summary: String,
  public val steps: Int,
  public val valueRepresentation: (Float) -> String,
  public val valueRange: ClosedFloatingPointRange<Float>,
  public val floatToType: (Float) -> A,
  public val typeToFloat: (A) -> Float,
) : PreferenceSettingItem<S, A>

public data class GroupSettingItem(
  override val title: String,
  override val enabled: Boolean = true,
  public val content: List<SettingItem>
) : SettingItem

public data class CallbackSettingItem(
  override val title: String,
  public val summary: String,
  @DrawableRes public val iconDrawable: Int = 0,
  override val enabled: Boolean = true,
  public val onClick: () -> Unit
) : SettingItem

public data class ButtonSettingItem(
  override val title: String,
  public val summary: String,
  public val buttonText: String,
  @DrawableRes public val iconDrawable: Int = 0,
  override val enabled: Boolean = true,
  public val onClick: () -> Unit
) : SettingItem
