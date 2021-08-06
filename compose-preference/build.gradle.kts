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

version = ComposePrefVersion.VERSION

plugins {
  id("com.android.library")
  kotlin("android")
  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")
}

android {
  compileSdk = SdkVersion.COMPILE

  defaultConfig {
    minSdk = SdkVersion.MIN
    targetSdk = SdkVersion.TARGET
    version = ComposePrefVersion.VERSION

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    debug {
      isTestCoverageEnabled = false
    }

    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  buildFeatures {
    // Enables Jetpack Compose for this module
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = Libs.AndroidX.Compose.VERSION
  }

  compileOptions {
    isCoreLibraryDesugaringEnabled = true
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  lint {
    isWarningsAsErrors = false
    isAbortOnError = false
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
  }

  packagingOptions {
    resources {
      excludes += listOf(
        "META-INF/AL2.0",
        "META-INF/LGPL2.1"
      )
    }
  }

  kotlinOptions {
    jvmTarget = "1.8"
    languageVersion = "1.5"
    apiVersion = "1.5"
    suppressWarnings = false
    verbose = true
    freeCompilerArgs = listOf(
      "-Xexplicit-api=warning",
      "-Xopt-in=kotlin.RequiresOptIn"
    )
  }
}

dependencies {
  coreLibraryDesugaring(Libs.DESUGAR)
  implementation(kotlin("stdlib-jdk8"))
  implementation(project(":pref-store"))
  implementation(Libs.AndroidX.APPCOMPAT)
  implementation(Libs.AndroidX.Ktx.CORE)
  implementation(Libs.Datastore.PREFERENCES)
  implementation(Libs.AndroidX.Lifecycle.RUNTIME_KTX)

  implementation(Libs.Coroutines.CORE)
  implementation(Libs.Coroutines.ANDROID)

  implementation(Libs.AndroidX.Compose.UI)
  implementation(Libs.AndroidX.Compose.TOOLING)
  implementation(Libs.AndroidX.Compose.FOUNDATION)
  implementation(Libs.AndroidX.Compose.MATERIAL)

  implementation(Libs.AndroidX.Activity.ACTIVITY_COMPOSE)
  implementation(Libs.AndroidX.Constraint.LAYOUT_COMPOSE)

  testImplementation(Libs.JUnit.JUNIT)
  testImplementation(Libs.AndroidX.Test.CORE) {
    exclude("junit", "junit")
  }
  testImplementation(Libs.AndroidX.Test.RULES) {
    exclude("junit", "junit")
  }
  testImplementation(Libs.Expect.EXPECT)
  testImplementation(Libs.Coroutines.TEST)

  androidTestImplementation(Libs.AndroidX.Test.RUNNER) {
    exclude("junit", "junit")
  }
  androidTestImplementation(Libs.AndroidX.Test.Ext.JUNIT) {
    exclude("junit", "junit")
  }
  androidTestImplementation(Libs.JUnit.JUNIT)
  androidTestImplementation(Libs.Expect.EXPECT)
  androidTestImplementation(Libs.Coroutines.TEST)
}
