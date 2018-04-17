@file:Suppress("FunctionName")

import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.observers.ResourceObserver
import io.reactivex.rxkotlin.toCompletable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import java.lang.Thread.sleep
import java.util.concurrent.*

/* Create an Observable directly, instead
of using an Observable factory method, emit
a few items, and terminate normally. */
fun ex_1_1() {
    Observable.create<Int> {
        it.onNext(1)
        it.onNext(2)
        it.onNext(3)
        it.onNext(4)
        it.onComplete()
    }.subscribe { println(it) }
}

/* Create an Observable directly, instead
of using an Observable factory method. Emit
a few items, and terminate abnormally. Have
the subscriber print the error. */
fun ex_1_2() {
    Observable.create<Int> {
        it.onNext(1)
        it.onNext(2)
        it.onNext(3)
        it.onNext(4)
        it.onError(RuntimeException("Error!"))
    }.subscribe ({ println(it) }, { println(it) })
}

/* Create an Observable directly, instead
of using an Observable factory method. Emit
a few items. Throw and catch an exception. Emit
the exception to terminate the subscription
abnormally. The subscriber should print the error. */
fun ex_2() {
    Observable.create<Int> {
        it.onNext(1)
        it.onNext(2)
        it.onNext(3)
        it.onNext(4)
        try {
            throw RuntimeException("Error!")
        } catch (e: Exception) {
            it.onError(e)
        }
    }.subscribe ({ println(it) }, { println(it) })
}

/* Create an Observable directly, instead
of using an Observable factory method. Emit
a few items. Derive new Observables by filtering
and mapping. */
fun ex_3() {
    Observable.create<Int> {
        it.onNext(1)
        it.onNext(2)
        it.onNext(3)
        it.onNext(4)
        it.onComplete()
    }.map { it * it }.filter {
        it.rem(2) == 0}.subscribe({
        println(it) })
}

/* Create an observable of some items, using a
factory method.  Derive new Observables by filtering
and mapping */
fun ex_5() {
    Observable.just(1,2,3,4).map { it * it }.filter {
        it.rem(2) == 0}.subscribe({
        println(it) })
}

/* Create an observable from an Iterable, using a factory
method.  Derive new Observables by filtering and mapping */
fun ex_6() {
    listOf(1,2,3,4).toObservable().map { it * it }.filter {
        it.rem(2) == 0}.subscribe({
        println(it) })
    Observable.fromIterable(listOf(1,2,3,4)).map { it * it }.filter {
        it.rem(2) == 0}.subscribe({
        println(it) })
}

// Implementing and subscribing to an Observer

// Subscribe with an instance of Observer.
fun ex_7() {
    listOf(1,2,3,4).toObservable().map { it * it }.filter {
        it.rem(2) == 0}.subscribe(object : Observer<Int> {
        override fun onComplete() {
            println("complete")
        }

        override fun onSubscribe(d: Disposable) {
            println("subscribed")
        }

        override fun onNext(t: Int) {
            println(t)
        }

        override fun onError(e: Throwable) {
            println(e)
        }
    })
}

// Shorthand Observers with lambdas

// Specify three lambda parameters for onNext, onError,
// and onComplete when subscribing.
fun ex_8() {
    listOf(1,2,3,4).toObservable().map { it * it }.filter {
        it.rem(2) == 0}.subscribe({
        println(it)
    }, {
        println(it.printStackTrace())
    }, {
        println("complete")
    })
}

// Omit onComplete lambda when subscribing.
fun ex_9() {
    listOf(1,2,3,4).toObservable().map { it * it }.filter {
        it.rem(2) == 0}.subscribe({
        println(it)
    }, {
        println(it.printStackTrace())
    })
}

// Omit onError lambda when subscribing.
fun ex_10() {
    listOf(1,2,3,4).toObservable().map { it * it }.filter {
        it.rem(2) == 0}.subscribe({
        println(it)
    })
}

// Cold vs hot Observables

// Cold Observables

// Cold Observable replays all events to each Observer.  All
// events are pushed to the second subscriber only after all
// have been pushed to the first.
fun ex_11() {
    listOf(1,2,3,4).toObservable().let {
        it.subscribe({
            println(it)
        })
        println()
        it.subscribe({
            println(it)
        })
    }
}

//ex_11()

// Cold Observable still creates one stream per subscriber even
// when stream is transformed with operators before subscription
// by one of the subscribers.
fun ex_12() {
    listOf(1,2,3,4).toObservable().let {
        it.subscribe {
            println(it)
        }
        println()
        it.map {
        it * it }.filter {
        it.rem(2) == 0}.subscribe {
            println(it)
        }
    }
}

//ex_12()

// Hot Observables

// ex_13 Skipped - JavaFX

// ConnectableObservable

// Transform an Observable into a ConnectableObservable and add two
// subscribers.  Events are emitted alternating between subscribers.
fun ex_14() {
    val connectableObservable = listOf(1,2,3,4).toObservable().publish()
    connectableObservable.map {
        (it + 64).toChar() }.subscribe {
        println("observer 1: $it") }
    println("should be no emissions so far")
    connectableObservable.subscribe { println("observer 2: $it") }
    println("should be no emissions so far")
    connectableObservable.connect()
}

//ex_14()

// Other Observable sources

// Observable.range()

// Emit consecutive range of integers
fun ex_15() {
    Observable.range(5, 10).subscribe { println(it) }
    Observable.rangeLong(5, 10).subscribe { println(it) }
}

//ex_15()

// Observable.interval()

// Emit event after initial delay period, at specified interval.
// Sleep the executing Thread because the Observable returned
// executes on the computation Scheduler by default.
fun ex_17() {
    println("running")
    Observable.interval(5, 1L, TimeUnit.SECONDS).let {
        it.subscribe { println("observer 1: $it") }
        sleep(10000)
    }
}

//ex_17()

// Observable.interval() returns a hot observable that restarts
// count from 0 with each new subscription.
fun ex_18() {
    println("running")
    Observable.interval(10, 1L, TimeUnit.SECONDS).let {
        it.subscribe { println("observer 1: $it") }
        sleep(5000)
        it.subscribe { println("observer 2: $it") }
        sleep(5000)
    }
}

//ex_18()

// Multicast Observable.interval() by turning it into a hot Observable.
fun ex_19() = Observable.interval(5,1,TimeUnit.SECONDS).publish().let {
    it.subscribe { println("observer 1: $it") }
    it.connect()
    sleep(10000)
    it.subscribe { println("observer 2: $it") }
    sleep(5000)
}

//ex_19()

// Observable.future()

// Transform a Future result into an Observable of the result.
fun ex_19_2() {
    val f = FutureTask<Int>({
        Thread.sleep(5000)
        1000
    })
    val e = Executors.newSingleThreadExecutor()
    e.execute(f)
    Observable.fromFuture(f).subscribe(::println, {}, { println("complete") })
    e.shutdown()
    e.awaitTermination(5, TimeUnit.SECONDS)
}

//ex_19_2()

// Observable.empty()

// Observable that emits only complete event, no data events.
fun ex_20() {
    Observable.empty<Int>().subscribe(
            ::println,
            Throwable::printStackTrace,
            { println("complete") })
}

//ex_20()

// Observable.never()

// Observable that never emits any events.
fun ex_21() {
    Observable.never<Int>().subscribe(
            ::println,
            Throwable::printStackTrace,
            { println("complete") })
    Thread.sleep(5000)
}

//ex_21()

// Observable.error()

// Observable that only emits an error event.
fun ex_22() {
    Observable.error<Int>(Exception("Dang!")).subscribe(
            ::println,
            Throwable::printStackTrace,
            { println("complete") })
    Thread.sleep(5000)
}

//ex_22()

// Observable that only emits an error event.  Error
// event is supplied by a factory lambda.
fun ex_23() {
    Observable.error<Int> {
        Exception("Dang!")
    }.subscribe(
            ::println,
            Throwable::printStackTrace,
            { println("complete") })
    Thread.sleep(5000)
}

//ex_23()

// Observable.defer()

// An Observable isn't sensitive to state changes in the interval
// between creation and subscription time.
fun ex_24() {
    var start = 1
    var count = 5
    Observable.range(start, count).let {
        it.subscribe({ println("observer 2: $it") },
                Throwable::printStackTrace,
                { println("complete") })
        start = 10
        count = 2
        it.subscribe({ println("observer 2: $it") },
                Throwable::printStackTrace,
                { println("complete") })
    }
}

//ex_24()

// Allows the behavior of the Observable to be determined by state variables
// at subscription time, rather than being fixed to state at creation time.
fun ex_25() {
    var start = 1
    var count = 5
    Observable.defer<Int> {
        Observable.range(start, count) }.let {
        it.subscribe({ println("observer 2: $it") },
                Throwable::printStackTrace,
                { println("complete") })
        start = 10
        count = 2
        it.subscribe({ println("observer 2: $it") },
                Throwable::printStackTrace,
                { println("complete") })
    }
}

//ex_25()

@Suppress("DIVISION_BY_ZERO")
fun ex_27() {
   Observable.fromCallable { 1.div(0) }.subscribe(
            ::println, {
       println("error")
       it.printStackTrace()
   })
}

//ex_27()

// Single, Completable, and Maybe

// Creating a Single directly
fun ex_28() {
    Single.just(3).subscribe(Consumer<Int> { println(it) })
}

//ex_28()

// An operator that returns a Single. 2 is provided as the Single's
// default value.  When the upstream Observable is empty, the default
// value is emitted by the Single.
fun ex_29() {
    Observable.just(3,4,5).let {
        it.first(2).subscribe(Consumer<Int> { println(it) })
    }
    Observable.empty<Int>().let {
        it.first(2).subscribe(Consumer<Int> { println(it) })
    }
}

//ex_29()

// Maybe

// A Maybe that's empty, and one that isn't, and another that observes with a
// MaybeObserver.
// onComplete is called when Maybe is empty.  If Maybe is not empty, onSuccess
// is called and onComplete is not.
fun ex_30() {
    Maybe.empty<Int>().subscribe({ println("observable 1 onSuccess: $it") },
            Throwable::printStackTrace, { println("observable 1 onComplete") })

    Maybe.just(1).subscribe({ println("observable 2 onSuccess: $it") },
            Throwable::printStackTrace, { println("observable 2 onComplete") })

    Maybe.empty<Int>().subscribe(object : MaybeObserver<Int> {
        override fun onSubscribe(d: Disposable) {}
        override fun onSuccess(value: Int) { println("observable 3 onSuccess: $value") }
        override fun onError(e: Throwable) { e.printStackTrace() }
        override fun onComplete() { println("observable 3 onComplete") }
    })
}

//ex_30()

// An operator that returns a Maybe
fun ex_31() {
    Observable.empty<Int>().firstElement().subscribe({ println("onSuccess: $it") },
            Throwable::printStackTrace, { println("onComplete") })
}

// ex_31()

// Completable

// Completable observable with a Runnable that executes before calling onComplete,
// and another that calls onComplete immediately.  Notice absence of type parameter
// on complete() and CompletableObserver.
fun ex_32() {
    Completable.complete().subscribe { println("complete") }
    Completable.complete().subscribe(object : CompletableObserver {
        override fun onSubscribe(d: Disposable) {}
        override fun onComplete() { println("complete") }
        override fun onError(e: Throwable) {}
    })
    Completable.fromRunnable { println("runnable running") }
            .subscribe { println("complete") }
}

//ex_32()

// Disposing

// Dispose an Observable.  Notice complete event is not emitted.
fun ex_33() {
    val disposable = Observable.interval(1, 1, TimeUnit.SECONDS)
            .subscribe ({ println(it) }, Throwable::printStackTrace, { println("complete") })
    Thread.sleep(4000)
    println("disposing")
    disposable.dispose()
    println("no more emissions expected")
    Thread.sleep(2000)
    println("no more emissions received")
    println("exiting")
}

//ex_33()

// Providing an Observer to subscribe() does not return a
// Disposable - the Observer must handle displosal.
// Provinding a ResourceObserver returns a Displosable.
fun ex_34() {
    Observable.interval(1, 1, TimeUnit.SECONDS)
            .subscribe(object : Observer<Long> {
                lateinit var disposable: Disposable

                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(t: Long) {
                    if (t.equals(4L)) {
                        println("disposing")
                        disposable.dispose()
                    }
                    else println(t)
                }

                override fun onComplete() {
                    println("complete")
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                }
            })
    Thread.sleep(6000)
    println("done")

    val d = Observable.interval(1, 1, TimeUnit.SECONDS)
            .subscribeWith(object : ResourceObserver<Long>() {
                override fun onComplete() {
                    println("complete")
                }

                override fun onError(e: Throwable) {
                }

                override fun onNext(t: Long) {
                    println(t)
                }
            })
    Thread.sleep(4000)
    println("disposing")
    d.dispose()
    println("waiting")
    Thread.sleep(3000)
    println("done")
}

//ex_34()

fun ex_35() {
    val cd = CompositeDisposable()
    Observable.interval(1, 1, TimeUnit.SECONDS).run {
        listOf(subscribe(::println), subscribe(::println))
                .forEach { cd.addAll(it) }
    }
    Thread.sleep(3000)
    println("disposing")
    cd.dispose()
    println("expecting no more emissions")
    Thread.sleep(3000)
    println("exiting")
}

//ex_35()

// Disposing via the emitter provided to Observable.create().
// Setting an action via setCancellable() that executes when
// disposal occurs.
fun ex_36() {
    val disposable = (Observable.create<Int> { emitter ->
        try {
            var i = 0
            emitter.setCancellable({ println("cancelled") })
            while (!emitter.isDisposed) {
                emitter.onNext(i++)
            }
            if (!emitter.isDisposed)
                emitter.onComplete()
        } catch (e: Throwable) {
            emitter.onError(e)
        }
    }).subscribeOn(Schedulers.computation()).subscribe(
            ::println, Throwable::printStackTrace, { println("complete") })
    println("waiting")
    Thread.sleep(2000)
    println("disposing")
    disposable.dispose()
    println("there should be no more emissions")
    Thread.sleep(3000)
}

ex_36()