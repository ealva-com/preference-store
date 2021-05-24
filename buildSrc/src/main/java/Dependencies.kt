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

object Sdk {
  const val MIN_SDK_VERSION = 21
  const val TARGET_SDK_VERSION = 30
  const val COMPILE_SDK_VERSION = 30
}

object AndroidxLibs {
  const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:${Versions.ACTIVITY_COMPOSE}"
  const val APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
  const val CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
  const val DATASTORE_PREFERENCES =
    "androidx.datastore:datastore-preferences:${Versions.DATASTORE}"
  const val LIFECYCLE_RUNTIME_KTX =
    "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}"
  const val CONSTRAINT_COMPOSE =
    "androidx.constraintlayout:constraintlayout-compose:${Versions.CONSTRAINT_COMPOSE}"
  const val COMPOSE_NAVIGATION =
    "androidx.navigation:navigation-compose:${Versions.COMPOSE_NAVIGATION}"
  const val MATERIAL = "com.google.android.material:material:${Versions.MATERIAL}"
  const val VIEWMODEL_COMPOSE =
    "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.VIEWMODEL_COMPOSE}"
}

object ComposeLibs {
  const val UI = "androidx.compose.ui:ui:${Versions.COMPOSE}"
  const val UI_TOOLING = "androidx.compose.ui:ui-tooling:${Versions.COMPOSE}"
  const val FOUNDATION = "androidx.compose.foundation:foundation:${Versions.COMPOSE}"
  const val MATERIAL = "androidx.compose.material:material:${Versions.COMPOSE}"
  const val MATERIAL_ICONS =
    "androidx.compose.material:material-icons-core:${Versions.COMPOSE}"
  const val MATERIAL_ICONS_EXT =
    "androidx.compose.material:material-icons-extended:${Versions.COMPOSE}"
  const val RUNTIME_LIVEDATA =
    "androidx.compose.runtime:runtime-livedata:${Versions.COMPOSE}"
}

object ThirdParty {
  const val COROUTINE_ANDROID =
    "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}"
  const val COROUTINE_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}"
  const val KOIN = "io.insert-koin:koin-core:${Versions.KOIN}"
  const val KOIN_ANDROID = "io.insert-koin:koin-android:${Versions.KOIN}"
}

object TestingLib {
  const val COROUTINE_TEST =
    "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES_TEST}"
  const val EXPECT = "com.nhaarman:expect.kt:${Versions.EXPECT}"
  const val JUNIT = "junit:junit:${Versions.JUNIT}"
}

object AndroidTestingLib {
  const val ANDROIDX_TEST_CORE = "androidx.test:core:${Versions.ANDROIDX_TEST}"
  const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_EXT}"
  const val ANDROIDX_TEST_RULES = "androidx.test:rules:${Versions.ANDROIDX_TEST}"
  const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:${Versions.ANDROIDX_TEST}"
}

object ToolsLib {
  const val DESUGARING = "com.android.tools:desugar_jdk_libs:${Versions.DESUGAR}"
}

object BuildPluginsVersion {
  const val AGP = "7.0.0-beta01"
  const val DETEKT = "1.17.0"
  const val DOKKA = "1.4.32"
  const val KOTLIN = "1.4.32"
  const val VANNIKTECH_PUBLISH = "0.15.1"
  const val VERSIONS = "0.38.0"
}

object Versions {
  const val ACTIVITY_COMPOSE = "1.3.0-alpha07"
  const val ANDROIDX_TEST = "1.4.0-alpha04"
  const val ANDROIDX_TEST_EXT = "1.1.3-alpha04"
  const val APPCOMPAT = "1.3.0"
  const val COMPOSE = "1.0.0-beta06"
  const val COMPOSE_NAVIGATION = "1.0.0-alpha10"
  const val CONSTRAINT_COMPOSE = "1.0.0-alpha06"
  const val CORE_KTX = "1.5.0"
  const val COROUTINES = "1.5.0"
  const val COROUTINES_TEST = "1.5.0"
  const val DATASTORE = "1.0.0-beta01"
  const val DESUGAR = "1.0.10"
  const val EXPECT = "1.0.1"
  const val JUNIT = "4.13.2"
  const val KOIN = "3.0.2"
  const val KOTLIN = "1.4.32"
  const val LIFECYCLE = "2.4.0-alpha01"
  const val MATERIAL = "1.3.0"
  const val VIEWMODEL_COMPOSE = "1.0.0-alpha04"
}
