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

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ealva.prefstore.test.shared.CoroutineRule
import com.nhaarman.expect.expect
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
public class BasePreferenceStoreTest {
  @get:Rule
  public var coroutineRule: CoroutineRule = CoroutineRule()

  @get:Rule
  public val tempFolder: TemporaryFolder = TemporaryFolder()

  @Suppress("DEPRECATION")
  @get:Rule
  public var thrown: ExpectedException = ExpectedException.none()

  private lateinit var dataStoreScope: TestCoroutineScope
  private lateinit var testFile: File
  private lateinit var singleton: PreferenceStoreSingleton<TestPrefs>

  @Before
  public fun setup() {
    testFile = tempFolder.newFile("dummy.preferences_pb")
    dataStoreScope = TestCoroutineScope(coroutineRule.testDispatcher + Job())
    singleton = PreferenceStoreSingleton(::TestPrefs, testFile, dataStoreScope)
  }

  @After
  public fun cleanup() {
    dataStoreScope.cleanupTestCoroutines()
  }

  @Test
  public fun testPrefStoreString(): Unit = coroutineRule.runBlockingTest {
    singleton { prefs ->
      prefs.edit { it[stringPref] = "first" }
      expect(prefs.stringPref()).toBe("first")
      prefs.stringPref.set("second")
      expect(prefs.stringPref()).toBe("second")
      prefs.stringPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe("second")
      }
      prefs.stringPref.set("third")
      prefs.stringPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe("third")
      }
    }
  }

  @Test
  public fun testPrefStoreInt(): Unit = coroutineRule.runBlockingTest {
    singleton { prefs ->
      prefs.edit { it[intPref] = 100 }
      expect(prefs.intPref()).toBe(100)
      prefs.intPref.set(-100)
      expect(prefs.intPref()).toBe(-100)
      prefs.intPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(-100)
      }
      prefs.intPref.set(1000)
      prefs.intPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(1000)
      }
    }
  }

  @Test
  public fun testPrefStoreBoolean(): Unit = coroutineRule.runBlockingTest {
    singleton { prefs ->
      prefs.edit { it[boolPref] = false }
      expect(prefs.boolPref()).toBe(false)
      prefs.boolPref.set(true)
      expect(prefs.boolPref()).toBe(true)
      prefs.boolPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(true)
      }
      prefs.boolPref.set(false)
      prefs.boolPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(false)
      }
    }
  }

  @Test
  public fun testPrefStoreFloat(): Unit = coroutineRule.runBlockingTest {
    singleton { prefs ->
      prefs.edit { it[floatPref] = 100.101F }
      expect(prefs.floatPref()).toBe(100.101F)
      prefs.floatPref.set(-100.201F)
      expect(prefs.floatPref()).toBe(-100.201F)
      prefs.floatPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(-100.201F)
      }
      prefs.floatPref.set(1000.505F)
      prefs.floatPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(1000.505F)
      }
    }
  }

  @Test
  public fun testPrefStoreLong(): Unit = coroutineRule.runBlockingTest {
    singleton { prefs ->
      prefs.edit { it[longPref] = 100L }
      expect(prefs.longPref()).toBe(100L)
      prefs.longPref.set(-100L)
      expect(prefs.longPref()).toBe(-100L)
      prefs.longPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(-100L)
      }
      prefs.longPref.set(1000L)
      prefs.longPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(1000L)
      }
    }
  }

  @Test
  public fun testPrefStoreDouble(): Unit = coroutineRule.runBlockingTest {
    singleton { prefs ->
      prefs.edit { it[doublePref] = 100.101 }
      expect(prefs.doublePref()).toBe(100.101)
      prefs.doublePref.set(-100.201)
      expect(prefs.doublePref()).toBe(-100.201)
      prefs.doublePref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(-100.201)
      }
      prefs.doublePref.set(Double.MAX_VALUE)
      prefs.doublePref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(Double.MAX_VALUE)
      }
    }
  }

  @Test
  public fun testPrefStoreEnum(): Unit = coroutineRule.runBlockingTest {
    singleton { prefs ->
      prefs.edit { it[enumPref] = TestEnum.One }
      expect(prefs.enumPref()).toBe(TestEnum.One)
      prefs.enumPref.set(TestEnum.Two)
      expect(prefs.enumPref()).toBe(TestEnum.Two)
      prefs.enumPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(TestEnum.Two)
      }
      prefs.enumPref.set(TestEnum.Three)
      prefs.enumPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(TestEnum.Three)
      }
    }
  }

  @Test
  public fun testPrefStoreReset(): Unit = coroutineRule.runBlockingTest {
    singleton { prefs ->
      prefs.edit {
        it[intPref] = 5555
        it[stringPref] = "testIt"
        it[boolPref] = true
        it[floatPref] = 5555.5555F
        it[longPref] = 5555L
        it[doublePref] = 6666.6666
        it[enumPref] = TestEnum.One
      }
      expect(prefs.intPref()).toBe(5555)
      expect(prefs.stringPref()).toBe("testIt")
      expect(prefs.boolPref()).toBe(true)
      expect(prefs.floatPref()).toBe(5555.5555F)
      expect(prefs.longPref()).toBe(5555L)
      expect(prefs.doublePref()).toBe(6666.6666)
      expect(prefs.enumPref()).toBe(TestEnum.One)

      prefs.resetToDefaultExcept { it === prefs.stringPref }
      expect(prefs.intPref()).toBe(prefs.intPref.default)
      expect(prefs.stringPref()).toBe("testIt")
      expect(prefs.boolPref()).toBe(prefs.boolPref.default)
      expect(prefs.floatPref()).toBe(prefs.floatPref.default)
      expect(prefs.longPref()).toBe(prefs.longPref.default)
      expect(prefs.doublePref()).toBe(prefs.doublePref.default)
      expect(prefs.enumPref()).toBe(prefs.enumPref.default)

      prefs.resetAllToDefault()
      expect(prefs.intPref()).toBe(prefs.intPref.default)
      expect(prefs.stringPref()).toBe(prefs.stringPref.default)
      expect(prefs.boolPref()).toBe(prefs.boolPref.default)
      expect(prefs.floatPref()).toBe(prefs.floatPref.default)
      expect(prefs.longPref()).toBe(prefs.longPref.default)
      expect(prefs.doublePref()).toBe(prefs.doublePref.default)
      expect(prefs.enumPref()).toBe(prefs.enumPref.default)
    }
  }
}

private enum class TestEnum {
  Unknown,
  One,
  Two,
  Three;
}

private class TestPrefs(
  dataStore: DataStore<Preferences>,
  stateFlow: StateFlow<Preferences>
) : BasePreferenceStore<TestPrefs>(dataStore, stateFlow) {
  val intPref = intPreference("int_pref", -1)
  val stringPref = stringPreference("string_pref", "nada")
  val boolPref = boolPreference("bool_pref", false)
  val floatPref = floatPreference("float_pref", Float.MIN_VALUE)
  val longPref = longPreference("long_pref", Long.MIN_VALUE)
  val doublePref = doublePreference("double_pref", Double.MIN_VALUE)
  val enumPref = enumByNamePreference("enum_pref", TestEnum.Unknown)
}
