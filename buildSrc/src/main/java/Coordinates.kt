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

object AppVersion {
  const val ID = "com.ealva.prefstore"

  const val VERSION_NAME = "1.0.0"
  const val VERSION_CODE = 1
}

private const val IS_SNAPSHOT = false

object PrefStoreVersion {
  // All parts of versioning can be up to 2 digits: 0-99
  private const val MAJOR = 0
  private const val MINOR = 7
  private const val PATCH = 4
  private const val BUILD = 0

  const val VERSION_CODE = MAJOR * 1000000 + MINOR * 10000 + PATCH * 100 + BUILD
  val VERSION = "$MAJOR.$MINOR.$PATCH-${buildPart(IS_SNAPSHOT, BUILD)}"
}

object ComposePrefVersion {
  // All parts of versioning can be up to 2 digits: 0-99
  private const val MAJOR = 0
  private const val MINOR = 7
  private const val PATCH = 4
  private const val BUILD = 0

  const val VERSION_CODE = MAJOR * 1000000 + MINOR * 10000 + PATCH * 100 + BUILD
  val VERSION = "$MAJOR.$MINOR.$PATCH-${buildPart(IS_SNAPSHOT, BUILD)}"
}

@Suppress("SameParameterValue")
private fun buildPart(isSnapshot: Boolean, build: Int): String =
  if (isSnapshot) "SNAPSHOT" else build.toString()
