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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

public class PreferenceStoreSingleton<T : PreferenceStore<T>>(
  private val storeMaker: (Storage) -> T,
  private val file: File,
  private val scope: CoroutineScope
) {
  @Volatile
  private var instance: T? = null
  private val mutex = Mutex()

  public constructor(
    maker: (Storage) -> T,
    context: Context,
    fileName: String,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
  ) : this(maker, context.preferencesDataStoreFile(fileName), scope)

  public suspend fun asFlow(): Flow<T> = instance().updateFlow.map { it.store }

  public suspend fun instance(): T {
    return instance ?: withContext(scope.coroutineContext) {
      mutex.withLock {
        instance ?: storeMaker(file.makeDataStoreStorage(scope)).also { instance = it }
      }
    }
  }
}

public suspend inline operator fun <T : PreferenceStore<T>, R> PreferenceStoreSingleton<T>.invoke(
  block: T.() -> R
): R = instance().block()
