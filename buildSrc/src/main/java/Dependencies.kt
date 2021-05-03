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

object Versions {
  const val ANDROIDX_STARTUP = "1.0.0"
  const val ANDROIDX_TEST = "1.4.0-alpha04"
  const val ANDROIDX_TEST_EXT = "1.1.3-alpha04"
  const val APPCOMPAT = "1.2.0"
  const val CONSTRAINT_LAYOUT = "2.0.1"
  const val CORE_KTX = "1.3.2"
  const val COROUTINES = "1.4.3"
  const val COROUTINES_TEST = "1.4.3"
  const val DESUGAR = "1.0.10"
  const val EALVALOG = "0.5.6-SNAPSHOT"
  const val ESPRESSO_CORE = "3.2.0"
  const val EXPECT = "1.0.1"
  const val FASTUTIL = "7.2.1"
  const val JUNIT = "4.13.2"
  const val KOIN = "2.2.2"
  const val KOTLIN = "1.4.32"
  const val KOTLIN_RESULT = "1.1.11"
  const val LIFECYCLE = "2.2.0"
  const val MOSHI = "1.11.0"
  const val OKHTTP = "4.9.1"
  const val OKHTTP_LOGGING = "4.9.1"
  const val RETROFIT = "2.9.0"
  const val ROBOLECTRIC = "4.5.1"
}

object BuildPluginsVersion {
  const val AGP = "7.0.0-alpha14"
  const val DETEKT = "1.16.0"
  const val DOKKA = "1.4.30"
  const val KOTLIN = "1.4.32"
  const val VANNIKTECH_PUBLISH = "0.14.2"
  const val VERSIONS = "0.33.0"
}

object SupportLibs {
  const val ANDROIDX_APPCOMPAT = "androidx.appcompat:appcompat:${Versions.APPCOMPAT}"
  const val ANDROIDX_CONSTRAINT_LAYOUT =
    "com.android.support.constraint:constraint-layout:${Versions.CONSTRAINT_LAYOUT}"
  const val ANDROIDX_CORE_KTX = "androidx.core:core-ktx:${Versions.CORE_KTX}"
  const val ANDROIDX_LIFECYCLE_RUNTIME_KTX =
    "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.LIFECYCLE}"
  const val ANDROIDX_STARTUP = "androidx.startup:startup-runtime:${Versions.ANDROIDX_STARTUP}"
}

object ThirdParty {
  const val COROUTINE_ANDROID =
    "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.COROUTINES}"
  const val COROUTINE_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.COROUTINES}"
  const val EALVALOG = "com.ealva:ealvalog:${Versions.EALVALOG}"
  const val EALVALOG_ANDROID = "com.ealva:ealvalog-android:${Versions.EALVALOG}"
  const val EALVALOG_CORE = "com.ealva:ealvalog-core:${Versions.EALVALOG}"
  const val FASTUTIL = "it.unimi.dsi:fastutil:${Versions.FASTUTIL}"
  const val KOIN = "org.koin:koin-core:${Versions.KOIN}"
  const val KOIN_ANDROID = "org.koin:koin-android:${Versions.KOIN}"
  const val KOTLIN_RESULT = "com.michael-bull.kotlin-result:kotlin-result:${Versions.KOTLIN_RESULT}"
  const val MOSHI = "com.squareup.moshi:moshi:${Versions.MOSHI}"
  const val MOSHI_CODEGEN = "com.squareup.moshi:moshi-kotlin-codegen:${Versions.MOSHI}"
  const val MOSHI_RETROFIT = "com.squareup.retrofit2:converter-moshi:2.9.0"
  const val OKHTTP = "com.squareup.okhttp3:okhttp:${Versions.OKHTTP}"
  const val OKHTTP_LOGGING = "com.squareup.okhttp3:logging-interceptor:${Versions.OKHTTP_LOGGING}"
  const val RETROFIT = "com.squareup.retrofit2:retrofit:${Versions.RETROFIT}"
}

object TestingLib {
  const val COROUTINE_TEST =
    "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.COROUTINES_TEST}"
  const val EXPECT = "com.nhaarman:expect.kt:${Versions.EXPECT}"
  const val JUNIT = "junit:junit:${Versions.JUNIT}"
  const val MOCKITO_INLINE = "org.mockito:mockito-inline:3.2.4"
  const val MOCKITO_KOTLIN = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"
  const val ROBOLECTRIC = "org.robolectric:robolectric:${Versions.ROBOLECTRIC}"
}

object AndroidTestingLib {
  const val ANDROIDX_TEST_CORE = "androidx.test:core:${Versions.ANDROIDX_TEST}"
  const val ANDROIDX_TEST_EXT_JUNIT = "androidx.test.ext:junit:${Versions.ANDROIDX_TEST_EXT}"
  const val ANDROIDX_TEST_RULES = "androidx.test:rules:${Versions.ANDROIDX_TEST}"
  const val ANDROIDX_TEST_RUNNER = "androidx.test:runner:${Versions.ANDROIDX_TEST}"
  const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:${Versions.ESPRESSO_CORE}"
}

object ToolsLib {
  const val DESUGARING = "com.android.tools:desugar_jdk_libs:${Versions.DESUGAR}"
}
