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
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

public interface PreferenceStoreSingleton<T : PreferenceStore<T>> {
  public suspend fun instance(): T

  public companion object {
    public operator fun <T : PreferenceStore<T>> invoke(
      maker: (Storage) -> T,
      context: Context,
      fileName: String,
      scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    ): PreferenceStoreSingleton<T> =
      PreferenceStoreSingletonImpl(maker, context.preferencesDataStoreFile(fileName), scope)

    /** File must have the extension "preferences_pb" */
    public operator fun <T : PreferenceStore<T>> invoke(
      maker: (Storage) -> T,
      file: File,
      scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
    ): PreferenceStoreSingleton<T> = PreferenceStoreSingletonImpl(maker, file, scope)
  }
}

public suspend inline operator fun <T : PreferenceStore<T>, R> PreferenceStoreSingleton<T>.invoke(
  block: T.() -> R
): R = instance().block()

private class PreferenceStoreSingletonImpl<T : PreferenceStore<T>>(
  private val storeMaker: (Storage) -> T,
  private val file: File,
  private val scope: CoroutineScope
) : PreferenceStoreSingleton<T> {
  @Volatile
  private var instance: T? = null
  private val mutex = Mutex()

  override suspend fun instance(): T {
    return instance ?: withContext(scope.coroutineContext) {
      mutex.withLock {
        instance ?: storeMaker(file.makeDataStoreStorage(scope)).also { instance = it }
      }
    }
  }
}
