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

plugins {
  id("com.android.application")
  kotlin("android")
  id("kotlin-android")
}

android {
  compileSdk = Sdk.COMPILE_SDK_VERSION

  defaultConfig {
    minSdk = Sdk.MIN_SDK_VERSION
    targetSdk = Sdk.TARGET_SDK_VERSION

    applicationId = AppCoordinates.APP_ID
    versionCode = AppCoordinates.APP_VERSION_CODE
    versionName = AppCoordinates.APP_VERSION_NAME
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    signingConfig = signingConfigs.getByName("debug")
  }

  buildTypes {
    debug {
      isTestCoverageEnabled = false
    }

    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  buildFeatures {
    // Enables Jetpack Compose for this module
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.0.0-beta06"
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
      "-XXLanguage:+InlineClasses",
      "-Xinline-classes",
      "-Xopt-in=kotlin.RequiresOptIn",
      "-Xskip-prerelease-check"
    )
  }
}

dependencies {
  coreLibraryDesugaring(ToolsLib.DESUGARING)
  implementation(kotlin("stdlib-jdk8"))
  implementation(project(":preference-store"))
  implementation(project(":compose-preference"))
  implementation(AndroidxLibs.APPCOMPAT)
  implementation(AndroidxLibs.CORE_KTX)
  implementation(AndroidxLibs.DATASTORE_PREFERENCES)
  implementation(AndroidxLibs.LIFECYCLE_RUNTIME_KTX)

  implementation(ThirdParty.COROUTINE_CORE)
  implementation(ThirdParty.COROUTINE_ANDROID)

  implementation(ThirdParty.KOIN)
  implementation(ThirdParty.KOIN_ANDROID)

  implementation(ComposeLibs.UI)
  implementation(ComposeLibs.UI_TOOLING)
  implementation(ComposeLibs.FOUNDATION)
  implementation(ComposeLibs.MATERIAL)
  implementation(AndroidxLibs.COMPOSE_NAVIGATION)

  implementation(AndroidxLibs.ACTIVITY_COMPOSE)
  implementation(AndroidxLibs.MATERIAL)

  testImplementation(TestingLib.JUNIT)
  testImplementation(AndroidTestingLib.ANDROIDX_TEST_CORE) {
    exclude("junit", "junit")
  }
  testImplementation(AndroidTestingLib.ANDROIDX_TEST_RULES) {
    exclude("junit", "junit")
  }
  testImplementation(TestingLib.EXPECT)

  androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_EXT_JUNIT) {
    exclude("junit", "junit")
  }
  androidTestImplementation(AndroidTestingLib.ANDROIDX_TEST_RUNNER) {
    exclude("junit", "junit")
  }
  androidTestImplementation(TestingLib.JUNIT)
  androidTestImplementation(TestingLib.EXPECT)
  androidTestImplementation(TestingLib.COROUTINE_TEST)

}
