preference-store
===========
Wrapper around DataStore<Preferences> providing a higher level abstraction and additional
functionality.

# Usage
## Creating a PreferenceStore
A simple example to show the basics - SimplePrefs.kt:
```kotlin
enum class AnEnum {
  First,
  Another;
}

/** SimplePrefsSingleton(SimplePrefs.Companion::make, androidContext(), fileName) */
typealias SimplePrefsSingleton = PreferenceStoreSingleton<SimplePrefs>

interface SimplePrefs : PreferenceStore<SimplePrefs> {
  val someBool: UnmappedPref<Boolean>
  val someInt: UnmappedPref<Int>
  val anEnum: StorePref<String, AnEnum>
  companion object {
    fun make(dataStore: DataStore<Preferences>, stateFlow: StateFlow<Preferences>): SimplePrefs =
      SimplePrefsImpl(dataStore, stateFlow)
  }
}

private class SimplePrefsImpl(
  dataStore: DataStore<Preferences>,
  stateFlow: StateFlow<Preferences>
) : BasePreferenceStore<SimplePrefs>(dataStore, stateFlow), SimplePrefs {
  override val someBool = boolPreference("some_bool", false)
  override val someInt = intPreference("some_int", 100)
  override val anEnum = enumByNamePreference("an_enum", AnEnum.First)
}
```
SimplePrefs has 3 preferences defined, including an Enum preference, and implements a
PreferenceStore parameterized by itself. It also defines a companion object function to instantiate
the concrete implementation.

SimplePrefsImpl implements the SimplePrefs interface and subclasses BasePreferenceStore.
BasePreferenceStore provides protected methods for creating all the primitive type preferences
supported by DataStore<Preferences>, adds an Enum type preference, and provides functions for
creating new preference types.

A typealias is defined to refer to a Singleton class which holds a single instance of SimplePrefs.

## Using the PreferenceStore
Using the PreferenceStore and preferences is straightforward:
```kotlin
private suspend fun setSomePrefs(simplePrefs: SimplePrefs) {
  simplePrefs.anEnum(AnEnum.First) // commits the single preference anEnum
  // commit preferences as a single unit
  simplePrefs.edit {
    it[someBool] = false
    it[someInt] = 100
    it[anEnum] = AnEnum.Another
  }
  // Setting a preference value is a suspending function, reading is not
  require(simplePrefs.anEnum() == AnEnum.Another) // invoke the preference to get it's value 
  require(!simplePrefs.someBool())
}
```
And example of creating the Singleton inside a Koin module:
```kotlin
single(named("SimplePrefs")) {
  SimplePrefsSingleton(
    maker = SimplePrefs.Companion::make,
    context = androidContext(),
    fileName = "SimplePrefs"
  )
}
```
Injecting the Singleton inside an Activity:
```kotlin
private val simplePrefsSingleton: SimplePrefsSingleton by inject(qualifier = named("SimplePrefs"))
```
There are 2 ways to obtain the PreferenceStore instance from the Singleton, an instance() function
and a inline invoke() function taking a lambda which is provided the instance.
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  // Obtaining the instance and setting preference values are suspend functions.
  lifecycleScope.launch {
    val prefs = simplePrefsSingleton.instance()

    simplePrefsSingleton { simplePrefs ->
      setSomePrefs(simplePrefs)
    }
  }
}
```
Each preference provides a Flow of it's values. As the preference value is committed a new value
is emitted from the flow.
```kotlin
private suspend fun watchSimplePrefsInt(simplePrefs: SimplePrefs) {
  simplePrefs.someInt.asFlow().collect {
    println("SimplePrefs.someInt changed: ${simplePrefs.someInt()}")
  }
}
```
## Extending With New Preference Types
It is easy to add other preference types. Only 2 functions are required: one function maps from
the actual type to the stored type and the 2nd function converts from stored to actual. Here is
an example of creating a base class adding types to be used.
```kotlin
@JvmInline
value class Millis(val value: Long) : Comparable<Millis> {
  override fun toString(): String = value.toString()

  override operator fun compareTo(other: Millis): Int = value.compareTo(other.value)

  companion object {
    val ZERO = Millis(0)
  }
}

@JvmInline
value class Volume(val value: Int) : Comparable<Volume> {
  override fun toString(): String = value.toString()

  override fun compareTo(other: Volume): Int = value.compareTo(other.value)

  companion object {
    val OFF = Volume(0)
    val HALF = Volume(50)
    val FULL = Volume(100)
  }
}

typealias VolumeRange = ClosedRange<Volume>

typealias MillisStorePref = StorePref<Long, Millis>
typealias VolumeStorePref = StorePref<Int, Volume>

open class BaseAppPrefStore<T : PreferenceStore<T>>(
  dataStore: DataStore<Preferences>,
  stateFlow: StateFlow<Preferences>
) : BasePreferenceStore<T>(dataStore, stateFlow) {
  protected fun millisPref(
    name: String,
    default: Millis,
    sanitize: ((Millis) -> Millis)? = null
  ): MillisStorePref = makePreference(name, default, ::Millis, { it.value }, sanitize)

  protected fun volumePref(
    name: String,
    default: Volume,
    sanitize: ((Volume) -> Volume)? = null
  ): VolumeStorePref = makePreference(name, default, ::Volume, { it.value }, sanitize)
}
```
Creating a preference requires a name, a default value, a function to map from stored to actual
type, a function to map from actual to stored type, and an optional sanitize function. The sanitize
function is called before the value is converted for storage and can accept, alter, or reject the
value.

This is an example using the new base class and adding a Sanitize function to one of the preferences:
```kotlin
val DUCK_VOLUME_RANGE: VolumeRange = Volume.OFF..Volume.FULL

private class AppPrefsImpl(
  dataStore: DataStore<Preferences>,
  stateFlow: StateFlow<Preferences>
) : BaseAppPrefStore<AppPrefs>(dataStore, stateFlow), AppPrefs {

  override val firstRun = boolPreference("first_run", true)
  override val lastScanTime = millisPref("last_scan_time", Millis.ZERO)

  /**
   * This preference includes a Sanitize function where it coerces the value to be within the
   * volume range. All preferences may have a Sanitize function to control what is stored (or
   * rejected as invalid)
   */
  override val duckVolume = volumePref("duck_volume", Volume.HALF) {
    it.coerceIn(DUCK_VOLUME_RANGE)
  }
}
```
A preference can be created for any type which can be converted to a supported primitive or String.
