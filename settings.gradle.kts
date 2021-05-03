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

include(":preference-store")
include(":app")

pluginManagement {
  resolutionStrategy {
    eachPlugin {
      if (requested.id.id == "com.android.library") {
        useModule("com.android.tools.build:gradle:${requested.version}")
      }
      if (requested.id.id == "com.android.application") {
        useModule("com.android.tools.build:gradle:${requested.version}")
      }
    }
  }
  repositories {
    jcenter()
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

rootProject.name = ("preference-store")
