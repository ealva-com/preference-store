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

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
  id("com.android.application") version BuildPluginsVersion.AGP apply false
  id("com.android.library") version BuildPluginsVersion.AGP apply false
  kotlin("android") version BuildPluginsVersion.KOTLIN apply false
  id("io.gitlab.arturbosch.detekt") version BuildPluginsVersion.DETEKT
  id("com.github.ben-manes.versions") version BuildPluginsVersion.VERSIONS
  id("org.jetbrains.dokka") version BuildPluginsVersion.DOKKA
  id("com.vanniktech.maven.publish") version BuildPluginsVersion.VANNIKTECH_PUBLISH
}

allprojects {
  repositories {
    jcenter()
    google()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
  }
}

subprojects {
  apply {
    plugin("io.gitlab.arturbosch.detekt")
  }

  detekt {
    config = rootProject.files("config/detekt/detekt.yml")
    reports {
      html {
        enabled = true
        destination = file("build/reports/detekt.html")
      }
    }
  }
}

buildscript {
  dependencies {
    classpath("com.android.tools.build:gradle:${BuildPluginsVersion.AGP}")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
  }
}

tasks.withType<DependencyUpdatesTask> {
  rejectVersionIf {
    isNonStable(candidate.version)
  }
  checkForGradleUpdate = true
}

fun isNonStable(version: String) = "^[0-9,.v-]+(-r)?$".toRegex().matches(version).not()
