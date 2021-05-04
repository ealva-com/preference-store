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
import kotlin.random.Random

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
    singleton {
      edit { it[stringPref] = "first" }
      expect(stringPref()).toBe("first")
      stringPref.set("second")
      expect(stringPref()).toBe("second")
      stringPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe("second")
      }
      stringPref.set("third")
      stringPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe("third")
      }
    }
  }

  @Test
  public fun testPrefStoreInt(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[intPref] = 100 }
      expect(intPref()).toBe(100)
      intPref.set(-100)
      expect(intPref()).toBe(-100)
      intPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(-100)
      }
      intPref.set(1000)
      intPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(1000)
      }
    }
  }

  @Test
  public fun testPrefStoreBoolean(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[boolPref] = false }
      expect(boolPref()).toBe(false)
      boolPref.set(true)
      expect(boolPref()).toBe(true)
      boolPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(true)
      }
      boolPref.set(false)
      boolPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(false)
      }
    }
  }

  @Test
  public fun testPrefStoreFloat(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[floatPref] = 100.101F }
      expect(floatPref()).toBe(100.101F)
      floatPref.set(-100.201F)
      expect(floatPref()).toBe(-100.201F)
      floatPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(-100.201F)
      }
      floatPref.set(1000.505F)
      floatPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(1000.505F)
      }
    }
  }

  @Test
  public fun testPrefStoreLong(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[longPref] = 100L }
      expect(longPref()).toBe(100L)
      longPref.set(-100L)
      expect(longPref()).toBe(-100L)
      longPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(-100L)
      }
      longPref.set(1000L)
      longPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(1000L)
      }
    }
  }

  @Test
  public fun testPrefStoreDouble(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[doublePref] = 100.101 }
      expect(doublePref()).toBe(100.101)
      doublePref.set(-100.201)
      expect(doublePref()).toBe(-100.201)
      doublePref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(-100.201)
      }
      doublePref.set(Double.MAX_VALUE)
      doublePref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(Double.MAX_VALUE)
      }
    }
  }

  @Test
  public fun testPrefStoreEnum(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[enumPref] = TestEnum.One }
      expect(enumPref()).toBe(TestEnum.One)
      enumPref.set(TestEnum.Two)
      expect(enumPref()).toBe(TestEnum.Two)
      enumPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(TestEnum.Two)
      }
      enumPref.set(TestEnum.Three)
      enumPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(TestEnum.Three)
      }
    }
  }

  @Test
  public fun testPrefStoreReset(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit {
        it[intPref] = 5555
        it[stringPref] = "testIt"
        it[boolPref] = true
        it[floatPref] = 5555.5555F
        it[longPref] = 5555L
        it[doublePref] = 6666.6666
        it[enumPref] = TestEnum.One
      }
      expect(intPref()).toBe(5555)
      expect(stringPref()).toBe("testIt")
      expect(boolPref()).toBe(true)
      expect(floatPref()).toBe(5555.5555F)
      expect(longPref()).toBe(5555L)
      expect(doublePref()).toBe(6666.6666)
      expect(enumPref()).toBe(TestEnum.One)

      resetToDefaultExcept { it === stringPref }
      expect(intPref()).toBe(intPref.default)
      expect(stringPref()).toBe("testIt")
      expect(boolPref()).toBe(boolPref.default)
      expect(floatPref()).toBe(floatPref.default)
      expect(longPref()).toBe(longPref.default)
      expect(doublePref()).toBe(doublePref.default)
      expect(enumPref()).toBe(enumPref.default)

      resetAllToDefault()
      expect(intPref()).toBe(intPref.default)
      expect(stringPref()).toBe(stringPref.default)
      expect(boolPref()).toBe(boolPref.default)
      expect(floatPref()).toBe(floatPref.default)
      expect(longPref()).toBe(longPref.default)
      expect(doublePref()).toBe(doublePref.default)
      expect(enumPref()).toBe(enumPref.default)
    }
  }

  @Test
  public fun testTightWriteReadLoop(): Unit = coroutineRule.runBlockingTest {
    val ran = Random.Default
    singleton {
      (1..100).map { ran.nextInt() }.forEach { number ->
        intPref(number)
        expect(intPref()).toBe(number)
      }
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
  preferences: Preferences
) : BasePreferenceStore<TestPrefs>(dataStore, preferences) {
  val intPref = intPreference("int_pref", -1)
  val stringPref = stringPreference("string_pref", "nada")
  val boolPref = boolPreference("bool_pref", false)
  val floatPref = floatPreference("float_pref", Float.MIN_VALUE)
  val longPref = longPreference("long_pref", Long.MIN_VALUE)
  val doublePref = doublePreference("double_pref", Double.MIN_VALUE)
  val enumPref = enumByNamePreference("enum_pref", TestEnum.Unknown)
}
