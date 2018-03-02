package uk.co.jakelee.kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit







class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        justTest()
        emitTest()
        intervalTest()
        disposeTest()
    }

    fun justTest() {
        var result = "" // Initially create an empty string
        val observable = Observable.just("Hello") // Create an observable that only emits a single string, immediately.
        observable.subscribe({ s -> result = s }) // On the observable, set a subscriber. Using a lambda, the subscriber's `onNext()` function is set to assign `result` to the emitted string.
        println(result) // `result` is now "Hello".
    }

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

    fun intervalTest() {
        Observable
                .interval(1, TimeUnit.SECONDS)
                .take(10)
                .subscribe( { s -> println(s) })
    }

    fun disposeTest() {
        val compositeDisposable = CompositeDisposable()
        compositeDisposable.add(Observable.just("Hello").subscribe({ s -> println(s) }))
        compositeDisposable.dispose()
    }
}
