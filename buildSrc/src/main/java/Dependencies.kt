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

object SdkVersion {
  const val COMPILE = 30
  const val MIN = 21
  const val TARGET = 30
}

object PluginsVersion {
  const val AGP = "7.0.0-beta03"
  const val DETEKT = "1.17.1"
  const val DOKKA = "1.4.32"
  const val KOTLIN = "1.5.10"
  const val PUBLISH = "0.15.1"
  const val VERSIONS = "0.39.0"
}

object Libs {
  const val AGP = "com.android.tools.build:gradle:${PluginsVersion.AGP}"
  const val DESUGAR = "com.android.tools:desugar_jdk_libs:1.1.5"

  object Kotlin {
    private const val VERSION = "1.5.10"
    const val KGP = "org.jetbrains.kotlin:kotlin-gradle-plugin:$VERSION"

    // const val STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$VERSION"
    // const val EXTENSIONS = "org.jetbrains.kotlin:kotlin-android-extensions:$VERSION"
  }

  object Coroutines {
    private const val VERSION = "1.5.0"
    const val CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$VERSION"
    const val ANDROID = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$VERSION"
    const val TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$VERSION"
  }

  object Koin {
    private const val VERSION = "3.1.0"
    const val CORE = "io.insert-koin:koin-core:$VERSION"
    const val ANDROID = "io.insert-koin:koin-android:$VERSION"
  }

  object JUnit {
    private const val VERSION = "4.13.2"
    const val JUNIT = "junit:junit:$VERSION"
  }

  object AndroidX {
    const val APPCOMPAT = "androidx.appcompat:appcompat:1.3.0"
    const val PALETTE = "androidx.palette:palette:1.0.0"

    object Ktx {
      const val CORE = "androidx.core:core-ktx:1.6.0-alpha03"
    }

    object Activity {
      const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:1.3.0-alpha08"
    }

    object Constraint {
      const val LAYOUT_COMPOSE = "androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha07"
    }

    object Compose {
      const val VERSION = "1.0.0-beta08"
      const val FOUNDATION = "androidx.compose.foundation:foundation:$VERSION"
      const val UI = "androidx.compose.ui:ui:$VERSION"
      const val MATERIAL = "androidx.compose.material:material:$VERSION"
      const val TOOLING = "androidx.compose.ui:ui-tooling:$VERSION"

//      const val RUNTIME = "androidx.compose.runtime:runtime:$VERSION"
//      const val LAYOUT = "androidx.compose.foundation:foundation-layout:${VERSION}"
//      const val MATERIAL_ICONS_EXTENDED =
//        "androidx.compose.material:material-icons-extended:${VERSION}"
    }

    object Lifecycle {
      private const val VERSION = "2.3.1"
      const val RUNTIME_KTX = "androidx.lifecycle:lifecycle-runtime-ktx:2.4.0-alpha01"

//    const val VIEW_MODEL_COMPOSE = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha05"
//    const val VIEW_MODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
    }

    object Test {
      private const val VERSION = "1.4.0-alpha04"
      const val CORE = "androidx.test:core:$VERSION"
      const val RULES = "androidx.test:rules:$VERSION"
      const val RUNNER = "androidx.test:runner:$VERSION"

      object Ext {
        private const val VERSION = "1.1.3-alpha04"
        const val JUNIT = "androidx.test.ext:junit-ktx:$VERSION"
      }

      // const val ESPRESSO_CORE = "androidx.test.espresso:espresso-core:3.2.0"
    }
  }

  object Datastore {
    const val PREFERENCES = "androidx.datastore:datastore-preferences:1.0.0-beta01"
  }

  object Expect {
    const val EXPECT = "com.nhaarman:expect.kt:1.0.1"
  }

  object Navigation {
    const val COMPOSE = "androidx.navigation:navigation-compose:2.4.0-alpha01"
  }
}
