preference-store
===========
Wrapper around DataStore<Preferences> providing a higher level abstraction and additional
functionality.

# Usage
Note: This library currently uses Kotlin 1.5 language features and alpha/beta versions of some tools
and libraries.

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
  val someBool: BoolPref
  val someInt: IntPref
  val anEnum: StorePref<String, AnEnum>

  companion object {
    fun make(storage: Storage): SimplePrefs = SimplePrefsImpl(storage)
  }
}

private class SimplePrefsImpl(
  storage: Storage
) : BasePreferenceStore<SimplePrefs>(storage), SimplePrefs {
  override val someBool by preference(false)
  override val someInt by preference(100)
  override val anEnum by enumByNamePref(AnEnum.First)
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
}
```
An example of creating the Singleton inside a Koin module:
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
Each preference provides a Flow of its values. As the preference value is committed the value
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

enum class DuckAction {
  Duck,
  Pause,
  DoNothing;
}

typealias MillisStorePref = StorePref<Long, Millis>
typealias VolumeStorePref = StorePref<Int, Volume>

open class BaseAppPrefStore<T : PreferenceStore<T>>(
  storage: Storage
) : BasePreferenceStore<T>(storage) {
  protected fun millisPref(
    default: Millis,
    customName: String? = null,
    sanitize: ((Millis) -> Millis)? = null,
  ): MillisStorePref = asTypePref(default, ::Millis, { it.value }, customName, sanitize)

  protected fun volumePref(
    default: Volume,
    customName: String? = null,
    sanitize: ((Volume) -> Volume)? = null,
  ): VolumeStorePref = asTypePref(default, ::Volume, { it.value }, customName, sanitize)
}
```
Creating a preference requires a name, a default value, a function to map from stored to actual
type, a function to map from actual to stored type, and an optional sanitize function. The sanitize
function is called before the value is converted for storage and can accept, alter, or reject the
value.

This is an example using the new base class and adding a Sanitize function to one of the preferences:
```kotlin
typealias AppPrefsSingleton = PreferenceStoreSingleton<AppPrefs>

/**
 * Define an interface for our preferences to facilitate injecting test stubs, managing
 * dependencies, and to hide implementation details. Clients only need know about this interface.
 */
interface AppPrefs : PreferenceStore<AppPrefs> {
  val firstRun: BoolPref // stores and provides Boolean
  val lastScanTime: StorePref<Long, Millis> // stores Long, provides value class Millis
  val duckAction: StorePref<String, DuckAction> // stored String, provides enum DuckAction
  val duckVolume: StorePref<Int, Volume> // Stores Int, provides value class Volume

  companion object {
    val DUCK_VOLUME_RANGE: VolumeRange = Volume.OFF..Volume.FULL

    /** Construct the AppPrefs implementation */
    fun make(storage: Storage): AppPrefs = AppPrefsImpl(storage)
  }
}

/**
 * Implement a BaseAppPrefStore, which we defined separately to include common types. We might
 * have different PreferenceStores in the app, for example 1 for the entire app, 1 for a particular
 * service, another for a difference service, etc. We would put all common preference
 * specializations in a base class.
 */
private class AppPrefsImpl(storage: Storage) : BaseAppPrefStore<AppPrefs>(storage), AppPrefs {

  override val firstRun by preference(true)
  override val lastScanTime by millisPref(Millis.ZERO)
  override val duckAction by enumByNamePref(DuckAction.Duck)

  /**
   * This preference includes a Sanitize function where it coerces the value to be within the
   * volume range. All preferences may have a Sanitize function to control what is stored (or
   * rejected as invalid)
   */
  override val duckVolume by volumePref(Volume.HALF) {
    it.coerceIn(DUCK_VOLUME_RANGE)
  }
}
```
A preference can be created for any type which can be converted to a supported primitive or String.

# Using and Contributing
## Latest Version
Ensure you are using the latest [published version][maven-preference-store] or the latest
[SNAPSHOT][preference-store-snapshot] if you want a bleeding-edge version.
## Pull/Change Requests
Suggestions and Pull Requests welcome. Also, if you have a question, or a proposed change, open an
issue for discussion.
## Future
I have plans on adding a library which has [Compose][compose] functions for preference UI.

[maven-preference-store]: https://search.maven.org/search?q=g:com.ealva%20AND%20a:preference-store
[preference-store-snapshot]: https://oss.sonatype.org/content/repositories/snapshots/com/ealva/preference-store/
[compose]: https://developer.android.com/jetpack/compose]
