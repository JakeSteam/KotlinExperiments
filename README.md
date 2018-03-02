# Kotlin RxJava 2 notes

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

* `.just(x)`: Creates an observable as a wrapper around other data types (e.g. strings, ints).
* `.fromIterable(x)` / `.fromArray(x)`: Creates an observable from an iterable / array, and emits them in the existing order.
* `.fromCallable(x)` / `.fromFuture(x)`: Creates an observable from a callable / future, useful for conversion from existing async code.
* `.interval(x, y)`: Creates an observable that emits the number of times run every x, using y for units.

Many modifiers are available when creating an observable:

* `.take(x)`: Specifies the maximum number of emissions to emit. 

### Subscribers / Observers
Subscribers (also known as observers) listen to the observables. Each observable can have 0-X subscribers. There are 3 main methods that can be called:

* `onNext()`: Called when a new item is emitted from the observable.
* `onComplete()`: Called when the observable terminates successfully.
* `onError()`: Called when the observable terminates unsuccessfully.

## Syntax
### Creating simple subscription between observable and subscriber
<pre>
    fun justTest() {
        var result = "" // Initially create an empty string
        val observable = Observable.just("Hello") // Create an observable that only emits a single string, immediately.
        observable.subscribe({ s -> result = s }) // On the observable, set a subscriber. Using a lambda, the subscriber's `onNext()` function is set to assign `result` to the emitted string.
        println(result) // `result` is now "Hello".
    }
</pre>

### Creating simple string emitter and outputting emissions
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





*Guide based on an excellent article by [Lars Vogel & Simon Scholz for Vogella](http://www.vogella.com/tutorials/RxJava/article.html)*. 