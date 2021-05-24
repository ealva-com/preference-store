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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ealva.prefstore.test.shared.CoroutineRule
import com.ealva.prefstore.test.shared.expect
import com.nhaarman.expect.expect
import com.nhaarman.expect.fail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
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
  private lateinit var otherFile: File

  @Before
  public fun setup() {
    testFile = tempFolder.newFile("dummy.preferences_pb")
    otherFile = tempFolder.newFile("another.preferences_pb")
    dataStoreScope = TestCoroutineScope(coroutineRule.testDispatcher + Job())
    singleton = PreferenceStoreSingleton(TestPrefs.Companion::make, testFile, dataStoreScope)
  }

  @After
  public fun cleanup() {
    dataStoreScope.cleanupTestCoroutines()
  }

  @Test
  public fun testStringPref(): Unit = coroutineRule.runBlockingTest {
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
  public fun testOptStringPref(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[optStringPref] = "first" }
      expect(optStringPref()).toBe("first")
      optStringPref.set("second")
      expect(optStringPref()).toBe("second")
      optStringPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe("second")
      }
      optStringPref.set("third")
      optStringPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe("third")
      }
    }
  }

  @Test
  public fun testIntPref(): Unit = coroutineRule.runBlockingTest {
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
  public fun testOptIntPref(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[optIntPref] = 100 }
      expect(optIntPref()).toBe(100)
      optIntPref.set(-100)
      expect(optIntPref()).toBe(-100)
      optIntPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(-100)
      }
      optIntPref.set(1000)
      optIntPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(1000)
      }
    }
  }

  @Test
  public fun testBooleanPref(): Unit = coroutineRule.runBlockingTest {
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
  public fun testOpBooleanPref(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[optBoolPref] = false }
      expect(optBoolPref()).toBe(false)
      optBoolPref.set(true)
      expect(optBoolPref()).toBe(true)
      optBoolPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(true)
      }
      optBoolPref.set(false)
      optBoolPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(false)
      }
    }
  }

  @Test
  public fun testFloatPref(): Unit = coroutineRule.runBlockingTest {
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
  public fun testOptFloatPref(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[optFloatPref] = 100.101F }
      expect(optFloatPref()).toBe(100.101F)
      optFloatPref.set(-100.201F)
      expect(optFloatPref()).toBe(-100.201F)
      optFloatPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(-100.201F)
      }
      optFloatPref.set(1000.505F)
      optFloatPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(1000.505F)
      }
    }
  }

  @Test
  public fun testLongPref(): Unit = coroutineRule.runBlockingTest {
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
  public fun testOptLongPref(): Unit = coroutineRule.runBlockingTest {
    singleton {
      expect(optLongPref.key.name).toBe("optLongPref")
      expect(optLongPref()).toBeNull()
      optLongPref(100)
      expect(optLongPref()).toBe(100)
      edit { it[optLongPref] = 200 }
      expect(optLongPref()).toBe(200)
      optLongPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(200)
      }
    }
  }

  @Test
  public fun testDoublePref(): Unit = coroutineRule.runBlockingTest {
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
  public fun testOptDoublePref(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[optDoublePref] = 100.101 }
      expect(optDoublePref()).toBe(100.101)
      optDoublePref.set(-100.201)
      expect(optDoublePref()).toBe(-100.201)
      optDoublePref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(-100.201)
      }
      optDoublePref.set(Double.MAX_VALUE)
      optDoublePref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(Double.MAX_VALUE)
      }
    }
  }

  @Test
  public fun testEnumPref(): Unit = coroutineRule.runBlockingTest {
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
  public fun testOptEnumPref(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit { it[optEnumPref] = TestEnum.One }
      expect(optEnumPref()).toBe(TestEnum.One)
      optEnumPref(TestEnum.Two)
      expect(optEnumPref()).toBe(TestEnum.Two)
      optEnumPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(TestEnum.Two)
      }
      optEnumPref.set(TestEnum.Three)
      optEnumPref.asFlow().take(1).toList().let { list ->
        expect(list.first()).toBe(TestEnum.Three)
      }
    }
  }

  @Test
  public fun testStringSetPref(): Unit = coroutineRule.runBlockingTest {
    val aSet = setOf("A", "B", "C", "Dog")
    val bSet = setOf("Eric", "Fred", "Cat", "Dog")
    singleton {
      edit { it[stringSetPref] = aSet }
      expect(stringSetPref()).toContainAll(aSet)
      stringSetPref(bSet)
      expect(stringSetPref()).toContainAll(bSet)
      stringSetPref.asFlow().take(1).firstOrNull()?.let { it ->
        expect(it).toContainAll(bSet)
      } ?: fail("Flow returned no Set<String>")
    }
  }

  @Test
  public fun testOptStringSetPref(): Unit = coroutineRule.runBlockingTest {
    val aSet = setOf("A", "B", "C", "Dog")
    val bSet = setOf("Eric", "Fred", "Cat", "Dog")
    singleton {
      expect(optStringSetPref()).toBeNull()
      edit { it[optStringSetPref] = aSet }
      expect(optStringSetPref()).toContainAll(aSet)
      optStringSetPref(bSet)
      expect(optStringSetPref()).toContainAll(bSet)
      optStringSetPref.asFlow().take(1).firstOrNull()?.let { set ->
        expect(set).toContainAll(bSet)
      } ?: fail("Flow returned null instead of Set<String>")
    }
  }

  @Test
  public fun testEnumSetSetPref(): Unit = coroutineRule.runBlockingTest {
    val aSet = setOf(TestEnum.One, TestEnum.Two)
    val bSet = setOf(TestEnum.Two, TestEnum.Three)
    singleton {
      edit { it[enumSet] = aSet }
      expect(enumSet()).toContainAll(aSet)
      enumSet(bSet)
      expect(enumSet()).toContainAll(bSet)
      enumSet.asFlow().take(1).firstOrNull()?.let { it ->
        expect(it).toContainAll(bSet)
      } ?: fail("Flow returned no Set<TestEnum>")
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

      edit {
        it.clear(intPref)
        it.clear(boolPref)
        it.clear(floatPref)
        it.clear(longPref)
        it.clear(doublePref)
        it.clear(enumPref)
      }
      expect(intPref()).toBe(intPref.default)
      expect(stringPref()).toBe("testIt")
      expect(boolPref()).toBe(boolPref.default)
      expect(floatPref()).toBe(floatPref.default)
      expect(longPref()).toBe(longPref.default)
      expect(doublePref()).toBe(doublePref.default)
      expect(enumPref()).toBe(enumPref.default)

      clear()
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

  @Test
  public fun testClearSomePrefs(): Unit = coroutineRule.runBlockingTest {
    singleton {
      edit {
        it[intPref] = 5555
        it[stringPref] = "testIt"
        it[boolPref] = !boolPref.default
        it[floatPref] = 5555.5555F
        it[longPref] = 5555L
        it[doublePref] = 6666.6666
        it[enumPref] = TestEnum.One
      }
      expect(intPref()).toBe(5555)
      expect(stringPref()).toBe("testIt")
      expect(boolPref()).toBe(!boolPref.default)
      expect(floatPref()).toBe(5555.5555F)
      expect(longPref()).toBe(5555L)
      expect(doublePref()).toBe(6666.6666)
      expect(enumPref()).toBe(TestEnum.One)

      clear(intPref, floatPref, enumPref)
      expect(intPref()).toBe(intPref.default)
      expect(stringPref()).toBe("testIt")
      expect(boolPref()).toBe(!boolPref.default)
      expect(floatPref()).toBe(floatPref.default)
      expect(longPref()).toBe(5555L)
      expect(doublePref()).toBe(6666.6666)
      expect(enumPref()).toBe(enumPref.default)
    }
  }

  @Test(expected = IllegalArgumentException::class)
  public fun testClearBadPreference(): Unit = coroutineRule.runBlockingTest {
    val other = OtherPrefs(otherFile.makeDataStoreStorage(dataStoreScope))
    singleton {
      clear(other.otherPref)
    }
  }
}

private enum class TestEnum {
  Unknown,
  One,
  Two,
  Three;
}

private interface TestPrefs : PreferenceStore<TestPrefs> {
  val intPref: IntPref
  val optIntPref: OptIntPref
  val stringPref: StringPref
  val optStringPref: OptStringPref
  val boolPref: BoolPref
  val optBoolPref: OptBoolPref
  val floatPref: FloatPref
  val optFloatPref: OptFloatPref
  val longPref: LongPref
  val optLongPref: OptLongPref
  val doublePref: DoublePref
  val optDoublePref: OptDoublePref
  val enumPref: PreferenceStore.Preference<String, TestEnum>
  val optEnumPref: PreferenceStore.Preference<String?, TestEnum?>
  val stringSetPref: StringSetPref
  val optStringSetPref: OptStringSetPref
  val enumSet: PreferenceStore.Preference<Set<String>, Set<TestEnum>>

  companion object {
    fun make(storage: Storage): TestPrefs = TestPrefsImpl(storage)
  }
}

private class TestPrefsImpl(storage: Storage) : BasePreferenceStore<TestPrefs>(storage), TestPrefs {
  override val intPref by preference(-1)
  override val optIntPref by optPreference<Int>()
  override val stringPref by preference("nada")
  override val optStringPref by optPreference<String>()
  override val boolPref by preference(false)
  override val optBoolPref by optPreference<Boolean>()
  override val floatPref by preference(Float.MIN_VALUE)
  override val optFloatPref by optPreference<Float>()
  override val longPref by preference(Long.MIN_VALUE)
  override val optLongPref by optPreference<Long>()
  override val doublePref by preference(Double.MIN_VALUE)
  override val optDoublePref by optPreference<Double>()
  override val enumPref by enumByNamePref(TestEnum.Unknown)
  override val optEnumPref by optEnumByNamePref<TestEnum>()
  override val stringSetPref: StringSetPref by preference(emptySet())
  override val optStringSetPref by optPreference<Set<String>>()
  override val enumSet by enumSetByNamePref(setOf(TestEnum.Unknown))
}

private class OtherPrefs(storage: Storage) : BasePreferenceStore<OtherPrefs>(storage) {
  val otherPref by preference(0)
}
