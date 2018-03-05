# RxJava 2 & RxAndroid notes

## Summary

RxJava is used for *Reactive Programming*, a term used to describe consumers reacting to data as it comes in. This is very useful for asynchronous programming.

Syntax / functionality was changed significantly with RxJava 2.

## Concepts
### Emitters / Observables
Observables are sources of data. They can omit 0-X items and optionally terminate with a success or failure. There are 5 different types:

* `Observable<T>`: Emits 0-X items, terminates with success / error.
* `Flowable<T>`: Emits 0-X items, terminates with success / error. Supports backpressure (limiting emits).
* `Single<T>`: Emits 1 item, or an error. Reactive version of a normal function call.
* `Maybe<T>`: Emits 0-1 items, or an error. Reactive version of an optional.
* `Completable`: Emits 0 items, terminates with success / error. Reactive version of a runnable.

Additionally, there are useful helper methods for creating observables. The following examples are static functions on `Observable`, but `Flowable`, `Maybe`, and `Single` have similar syntax:

* `.create(x)`: Creates an observable from scratch.
* `.just(x)`: Creates an observable as a wrapper around other data types (e.g. strings, ints).
* `.from(x)`: Creates an observable from an iterable / array / callable / future, and emits them in the existing order.
* `.interval(x, y)` / `timer(x, y)`: Creates an observable that emits the number of times run every x (interval) / after x (timer), using y for units.
* `.range(x, y)`: Emits y values, starting at x.
* `.empty()` / `.error()` / `.never()`: Creates an observable that emits nothing, and then completes / errors / does nothing.

The emitter output can also be modified via various functions:

* `.take(x)`: Specifies the maximum number of emissions to emit.

### Subscribers / Observers
Subscribers (also known as observers) listen to the observables. Each observable can have 0-X subscribers. There are 3 main methods that can be called:

* `onNext()`: Called when a new item is emitted from the observable.
* `onComplete()`: Called when the observable terminates successfully.
* `onError()`: Called when the observable terminates unsuccessfully.

### Disposables
Disposables are created when adding a subscriber to an emitter. Retaining a reference to a disposable (`val disposable = e.subscribe(s)`) allows it to be cleaned up when the subscription is no longer necessary, via `.dispose()`.

A CompositeDisposable is a collection of disposables, useful for disposing of multiple subscriptions at once. It can be used via:

* `compositeDisposable.add(disposable)`: Add a new disposable.
* `compositeDisposable.dispose()`: Dispose all disposables.

### Schedulers
Schedulers are thread pools used for managing one or more threads. When a task needs to be executed, the scheduler decides which thread the task will be run on. 

A scheduler can be assigned to two places during the subscription process:

* `subscribeOn()`: Assigns a scheduler for doing the observer function.
* `observeOn()`: Assigns a scheduler for receiving the results of the observer function.

The following schedulers are always available via static references on `Schedulers`, whilst `AndroidSchedulers.mainThread` is RxAndroid specific and provides the UI / main thread:

* `Schedulers.io()`: Used for I/O work.
* `Schedulers.computation()`: Used for large, CPU intensive tasks.
* `Schedulers.newThread()`: Creates a new thread, very expensive and generally avoided.
* `Schedulers.single()`: A single thread that executes all tasks in the order added.
* `Schedulers.trampoline()`: A single thread that executes all tasks, starting with the most recently added. 

*Note: These can be overridden using `RxAndroidPlugins.setInitMainThreadSchedulerHandler { _ -> Schedulers.x }`*

## Samples
### Creating simple subscription between observable and subscriber
<pre>
    fun justTest() {
        var result = "" // Initially create an empty string
        val observable = Observable.just("Hello") // Create an observable that only emits a single string, immediately.
        observable.subscribe({ s -> result = s }) // On the observable, set a subscriber. Using a lambda, the subscriber's `onNext()` function is set to assign `result` to the emitted string.
        println(result) // `result` is now "Hello".
    }
</pre>

### Creating simple string emitter and printing observed strings
<pre>
    fun emitTest() {
        // Create an observable that loops through a list of strings and emits each (`onNext()`).
        // If anything goes wrong, call `onError()`, otherwise `onComplete()`.
        val strings = listOf("A", "B", "C")
        val stringEmittingObservable = Observable.create<String> { emitter ->
            try {
                for (string in strings) {
                    emitter.onNext(string)
                }
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }

        // Print out each emitted string
        stringEmittingObservable.subscribe({ s -> println(s) })
    }
</pre>

### [JUnit](https://junit.org/junit5/) testing emitters with [Mockito](https://github.com/mockito/mockito) using the MVP architecture. 
In this sample, the use case is implementing testing for a view without direct access to the existing observer. `exampleObservable` is an existing method, in this case triggered by tapping on a button. It is of type `Observable<Unit>`.
<pre>
    // `exampleEmitter` stores an updated reference to `e` (the existing emitter), so that it can be invoked directly.
    private lateinit var exampleEmitter: ObservableEmitter<Unit>

    @Before
    fun testSetup() {
        // Using Mockito, add an observer to `exampleObservable`. Whenever `exampleObservable` emits, update the local reference to the emitter (`exampleEmitter`).
    	whenever(exampleObservable).thenReturn(Observable.create { e ->
    	    exampleEmitter = e
    	})
    }

    @Test
    fun testButtonTap() {
        // Trigger the observable manually, invoking both the original and additional observers.
        exampleEmitter.onNext(Unit)

        // `assertEquals()` and `verify()` statements would go here, to ensure the correct effects were made.
    }

</pre>


*Guide based on an excellent article by [Lars Vogel & Simon Scholz for Vogella](http://www.vogella.com/tutorials/RxJava/article.html)*. 