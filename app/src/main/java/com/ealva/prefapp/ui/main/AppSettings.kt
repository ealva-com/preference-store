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

import android.content.res.Resources
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import com.ealva.comppref.pref.CallbackSettingItem
import com.ealva.comppref.pref.CheckboxSettingItem
import com.ealva.comppref.pref.GroupSettingItem
import com.ealva.comppref.pref.ListSettingItem
import com.ealva.comppref.pref.MultiSelectListSettingItem
import com.ealva.comppref.pref.SettingItem
import com.ealva.comppref.pref.SettingsScreen
import com.ealva.comppref.pref.SliderSettingItem
import com.ealva.comppref.pref.SwitchSettingItem
import com.ealva.prefapp.R
import com.ealva.prefapp.prefs.AppPrefs
import com.ealva.prefapp.prefs.AppPrefsSingleton
import com.ealva.prefapp.prefs.DuckAction
import com.ealva.prefapp.prefs.ItemType
import com.ealva.prefapp.prefs.Millis
import com.ealva.prefapp.prefs.MillisRange
import com.ealva.prefapp.prefs.Volume
import com.ealva.prefapp.prefs.VolumeRange
import com.ealva.prefapp.ui.AppColors
import com.ealva.prefstore.store.BoolPref
import com.ealva.prefstore.store.PreferenceStore
import com.ealva.prefstore.store.PreferenceStoreSingleton
import com.ealva.prefstore.store.StorePref
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.get
import kotlin.math.roundToInt
import kotlin.math.roundToLong

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun AppSettings(
  actions: Actions,
  navigateUp: () -> Unit,
  lifecycle: Lifecycle,
  prefsSingleton: AppPrefsSingleton = get(PreferenceStoreSingleton::class.java, named("AppPrefs"))
) {
  val scope = rememberCoroutineScope()
  val settingsState: MutableState<List<SettingItem>> = remember {
    mutableStateOf(listOf())
  }
  val resources = LocalContext.current.resources
  LaunchedEffect(settingsState) {
    scope.launch {
      prefsSingleton
        .asFlow()
        .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
        .collect { prefs -> settingsState.value = makeSettings(prefs, actions, resources) }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        backgroundColor = AppColors.primarySurface,
        contentColor = contentColorFor(AppColors.primarySurface),
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

private fun makeSettings(
  prefs: AppPrefs,
  actions: Actions,
  resources: Resources
): List<SettingItem> = listOf(
  makeFirstRunSetting(prefs.enableGroup, resources),
  makeDuckActionSetting(prefs.duckAction, resources),
  makeDuckVolumeSetting(prefs.duckVolume, prefs.duckAction, resources),
  makeCrossFadeLengthSetting(prefs.crossFadeLength, resources),
  GroupSettingItem(
    resources.getString(R.string.Group),
    enabled = prefs.enableGroup(),
    content = listOf(
      makeAutoDownloadSetting(prefs.autoDownload, resources),
      makeItemTypeSetting(prefs.itemType, resources),
      CallbackSettingItem(
        title = "Callback Setting",
        summary = "Go to Simple Settings",
        onClick = actions.simpleSettings
      )
    )
  ),
)

private fun makeItemTypeSetting(
  itemType: PreferenceStore.Preference<Set<String>, Set<ItemType>>,
  resources: Resources
): MultiSelectListSettingItem<ItemType> = MultiSelectListSettingItem(
  preference = itemType,
  title = resources.getString(R.string.MultipleSelectSetting),
  dialogItems = mapOf(
    "Type 1" to ItemType.Type1,
    "Type 2" to ItemType.Type2,
    "Type 3" to ItemType.Type3
  )
)

@Suppress("MagicNumber")
private fun makeCrossFadeLengthSetting(
  crossFadeLength: PreferenceStore.Preference<Long, Millis>,
  resources: Resources
): SliderSettingItem<Long, Millis> = SliderSettingItem(
  preference = crossFadeLength,
  title = resources.getString(R.string.CrossFadeLength),
  singleLineTitle = true,
  summary = resources.getString(R.string.CrossFadeLengthSummary),
  steps = ((AppPrefs.CROSS_FADE_RANGE.endInclusive - AppPrefs.CROSS_FADE_RANGE.start).value / 100L)
    .toInt() - 2,
  valueRepresentation = { it.roundTo100Millis().toString() },
  valueRange = crossFadeFloatRange,
  floatToType = { Millis(it.roundTo100Millis()) },
  typeToFloat = millisToFloat
)

private fun makeDuckVolumeSetting(
  duckVolume: PreferenceStore.Preference<Int, Volume>,
  duckAction: StorePref<String, DuckAction>,
  resources: Resources
): SliderSettingItem<Int, Volume> = SliderSettingItem(
  preference = duckVolume,
  title = resources.getString(R.string.DuckVolume),
  singleLineTitle = true,
  enabled = duckAction() == DuckAction.Duck,
  icon = Icons.Outlined.Face,
  summary = resources.getString(R.string.DuckVolumeSummary),
  steps = AppPrefs.DUCK_VOLUME_RANGE.endInclusive.value - 2,
  valueRepresentation = floatToIntString,
  valueRange = volumeFloatRange,
  floatToType = floatToVolume,
  typeToFloat = volumeToFloat
)

private fun makeDuckActionSetting(
  preference: StorePref<String, DuckAction>,
  resources: Resources
) = ListSettingItem(
  preference = preference,
  title = resources.getString(R.string.DuckAction),
  singleLineTitle = true,
  enabled = true,
  dialogItems = mapOf(
    "Duck" to DuckAction.Duck,
    "Pause" to DuckAction.Pause,
    "Do Nothing" to DuckAction.DoNothing
  )
)

private fun makeFirstRunSetting(enableGroup: BoolPref, resources: Resources) = SwitchSettingItem(
  preference = enableGroup,
  title = resources.getString(R.string.EnableGroup),
  summary = resources.getString(R.string.EnableGroupSummary),
  singleLineTitle = true,
)

private fun makeAutoDownloadSetting(
  autoDownload: BoolPref,
  resources: Resources
) = CheckboxSettingItem(
  preference = autoDownload,
  title = resources.getString(R.string.AutoDownload),
  summary = resources.getString(
    if (autoDownload()) R.string.AutomaticallyDownload else R.string.UseRemote
  ),
  singleLineTitle = true,
)

@JvmName("volumeToFloatRange")
private fun VolumeRange.toFloatRange(): ClosedFloatingPointRange<Float> =
  start.value.toFloat()..endInclusive.value.toFloat()

@JvmName("millisToFloatRange")
private fun MillisRange.toFloatRange(): ClosedFloatingPointRange<Float> =
  start.value.toFloat()..endInclusive.value.toFloat()

@Suppress("MagicNumber")
private fun Float.roundTo100Millis(): Long = div(100).roundToLong().times(100)

private val floatToIntString: (Float) -> String = { it.roundToInt().toString() }
private val volumeFloatRange = AppPrefs.DUCK_VOLUME_RANGE.toFloatRange()
private val floatToVolume: (Float) -> Volume = { Volume(it.roundToInt()) }
private val volumeToFloat: (Volume) -> Float = { it.value.toFloat() }
private val crossFadeFloatRange = AppPrefs.CROSS_FADE_RANGE.toFloatRange()
private val millisToFloat: (Millis) -> Float = { it.value.toFloat() }

