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
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

public typealias PreferenceStoreMaker<T> = (DataStore<Preferences>, StateFlow<Preferences>) -> T

public interface PreferenceStoreSingleton<T : PreferenceStore<T>> {
  public suspend fun instance(): T

  public companion object {
    public operator fun <T : PreferenceStore<T>> invoke(
      maker: PreferenceStoreMaker<T>,
      context: Context,
      fileName: String,
      scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    ): PreferenceStoreSingleton<T> {
      return PreferenceStoreSingletonImpl(maker, context.preferencesDataStoreFile(fileName), scope)
    }

    /** For test. File must have the extension "preferences_pb" */
    public operator fun <T : PreferenceStore<T>> invoke(
      maker: PreferenceStoreMaker<T>,
      file: File,
      scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    ): PreferenceStoreSingleton<T> = PreferenceStoreSingletonImpl(maker, file, scope)
  }
}

public suspend inline operator fun <T : PreferenceStore<T>, R> PreferenceStoreSingleton<T>.invoke(
  block: (T) -> R
): R = block(instance())

private class PreferenceStoreSingletonImpl<T : PreferenceStore<T>>(
  private val storeMaker: PreferenceStoreMaker<T>,
  private val file: File,
  private val scope: CoroutineScope
) : PreferenceStoreSingleton<T> {
  @Volatile
  private var instance: T? = null
  private val mutex = Mutex()

  override suspend fun instance(): T {
    return instance ?: withContext(scope.coroutineContext) {
      mutex.withLock { instance ?: make(storeMaker, file, scope).also { instance = it } }
    }
  }
}

private suspend fun <T : PreferenceStore<T>> make(
  storeMaker: PreferenceStoreMaker<T>,
  file: File,
  scope: CoroutineScope
): T {
  val dataStore = PreferenceDataStoreFactory.create(scope = scope) { file }
  return storeMaker(dataStore, dataStore.data.stateIn(scope))
}
