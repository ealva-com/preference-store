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

object AppCoordinates {
  const val APP_ID = "com.ealva.prefstore"

  const val APP_VERSION_NAME = "1.0.0"
  const val APP_VERSION_CODE = 1
}

object PreferenceStoreCoordinates {
  // All parts of versioning can be up to 2 digits: 0-99
  private const val versionMajor = 0
  private const val versionMinor = 5
  private const val versionPatch = 3
  private const val versionBuild = 0

  const val LIBRARY_VERSION_CODE = versionMajor * 1000000 + versionMinor * 10000 +
    versionPatch * 100 + versionBuild
  const val LIBRARY_VERSION = "$versionMajor.$versionMinor.$versionPatch-$versionBuild"
}
