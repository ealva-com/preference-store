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

package com.ealva.prefstore.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import java.io.File

/**
 * Storage is a persistent store of Preferences which can be edited and provides a flow of
 * changes.
 */
public interface Storage {
  /** A Preferences flow which emits if any preference is modified */
  public val data: Flow<Preferences>

  /** Calls [transform] with a [MutablePreferences] for batch editing of preferences */
  public suspend fun edit(transform: suspend (MutablePreferences) -> Unit)

  /** Get the value for [key], returning null if it does not exist*/
  public operator fun <T> get(key: Preferences.Key<T>): T?

  /** Clear all preferences */
  public suspend fun clear()
}

private const val FILE_EXT = "preferences_pb"

public suspend fun File.makeDataStoreStorage(scope: CoroutineScope): Storage {
  require(extension == FILE_EXT) { "File must have extension '$FILE_EXT'" }
  val dataStore = PreferenceDataStoreFactory.create(scope = scope) { this }
  return DataStoreStorage(dataStore, dataStore.data.take(1).first())
}

@Suppress("unused")
public suspend fun Context.makeDataStoreStorage(fileName: String, scope: CoroutineScope): Storage =
  preferencesDataStoreFile(fileName).makeDataStoreStorage(scope)

private class DataStoreStorage(
  private val dataStore: DataStore<Preferences>,
  private var lastPreferences: Preferences
) : Storage {
  override val data: Flow<Preferences>
    get() = dataStore.data

  override suspend fun edit(transform: suspend (MutablePreferences) -> Unit) {
    lastPreferences = dataStore.edit(transform)
  }

  override fun <T> get(key: Preferences.Key<T>): T? = lastPreferences[key]

  override suspend fun clear() {
    lastPreferences = dataStore.edit { it.clear() }
  }

  override fun toString(): String = lastPreferences.toString()
}

