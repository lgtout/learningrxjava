import io.reactivex.Observable
import io.reactivex.Observable.zip
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.computation
import java.lang.Thread.sleep
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun intenseCalculation(i: Int = 0): Int {
    sleep(ThreadLocalRandom.current().nextLong(2000))
    return i
}

fun subscribeOn() {
    Observable.range(1,5)
            .map {
                intenseCalculation()
                it
            }.subscribe { println(it) }
    Observable.just('a','b','c','d','e')
            .map {
                intenseCalculation()
                it
            }.subscribe { println(it) }
    println()
    Observable.range(1,5)
            .map {
                intenseCalculation()
                it
            }.subscribeOn(computation())
            .subscribe { println(it) }
    Observable.just('a','b','c','d','e')
            .map {
                intenseCalculation()
                it
            }.subscribe { println(it) }
    sleep(10000)
    println()
    val o1 = Observable.range(1,5)
            .map {
                intenseCalculation()
                it
            }.subscribeOn(computation())
    val o2 = Observable.just('a','b','c','d','e')
            .map {
                intenseCalculation()
                it
            }.subscribeOn(computation())
           zip(o1, o2, BiFunction { a: Int, b: Char -> Pair(a, b) }).subscribe { println(it) }
    sleep(10000)
}
//subscribeOn()

fun blockingSubscribe() {
    val o1 = Observable.range(1,5)
            .map {
                intenseCalculation()
                it
            }.subscribeOn(computation())
    val o2 = Observable.just('a','b','c','d','e')
            .map {
                intenseCalculation()
                it
            }.subscribeOn(computation())
    zip(o1, o2, BiFunction { a: Int, b: Char -> Pair(a, b) }).blockingSubscribe { println(it) }
}
//blockingSubscribe()

fun from() {
    val e = Executors.newFixedThreadPool(10)
    val s = Schedulers.from(e)
    Observable.range(1,5)
            .subscribeOn(s)
            .doFinally(e::shutdown)
            .subscribe { println(it) }
}
//from()

fun subscribeOn2() {
    Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(computation())
            .filter { it > 2 }
            .map { "$it" }
            .subscribe { println(it) }
    println()
    Observable.range(1, 5)
            .subscribeOn(computation())
            .filter { it > 2 }
            .map { "$it" }
            .subscribe { println("o1: $it ${ Thread.currentThread().name }") }
    Observable.range(1, 5)
            .subscribeOn(computation())
            .filter { it > 2 }
            .map { "$it" }
            .subscribe { println("o2: $it ${ Thread.currentThread().name }") }
    sleep(100)
    println()
    Observable.range(1, 5)
            .subscribeOn(computation())
            .filter { it > 2 }
            .map { "$it" }
            .publish().autoConnect(2).apply {
                subscribe { println("o1: $it ${ Thread.currentThread().name }") }
                subscribe { println("o2: $it ${ Thread.currentThread().name }") }
            }
    sleep(100)
}
//subscribeOn2()

fun subscribeOn3() {
    Observable.interval(1, TimeUnit.SECONDS, Schedulers.newThread()).let {
        it.subscribe { println("o1: $it ${ Thread.currentThread().name }") }.let {
            sleep(4000)
            it.dispose()
        }
    }
    println()
    Observable.range(1, 5)
            .subscribeOn(Schedulers.computation())
            .filter { it > 3 }
            .subscribeOn(Schedulers.io()).let {
                it.subscribe { println("o2: $it ${ Thread.currentThread().name }") }.let {
                    sleep(4000)
                    it.dispose()
                }
            }
    println()
    Observable.range(1, 5)
            .subscribeOn(Schedulers.computation())
            .filter {
                println("filter: o3: $it ${ Thread.currentThread().name }")
                it > 3
            }
            .observeOn(Schedulers.io())
            .map {
                println("map 1: o3: $it ${ Thread.currentThread().name }")
                it * 2
            }
            .observeOn(Schedulers.newThread())
            .map {
                println("map 2: o3: $it ${ Thread.currentThread().name }")
                "$it"
            }
            .observeOn(Schedulers.single())
            .subscribe { println("subscribe: o3: $it ${ Thread.currentThread().name }") }.let {
                sleep(1000)
                it.dispose()
            }
    println()
    Observable.range(1, 5)
            .subscribeOn(Schedulers.computation())
            .filter {
                println("filter: o3: $it ${ Thread.currentThread().name }")
                it > 3
            }
            .map {
                println("map 1: o3: $it ${ Thread.currentThread().name }")
                it * 2
            }
            .map {
                println("map 2: o3: $it ${ Thread.currentThread().name }")
                "$it"
            }
            .subscribe { println("subscribe: o3: $it ${ Thread.currentThread().name }") }.let {
                sleep(1000)
                it.dispose()
            }
}
//subscribeOn3()

fun flatMap() {
    Observable.range(1, 10)
            .flatMap {
                Observable.just(it)
                        .subscribeOn(Schedulers.computation())
                        .map { intenseCalculation(it) }
            }.apply {
                subscribe { println("subscribe: o1: $it ${ Thread.currentThread().name }") }.let {
                    sleep(5000)
                    it.dispose()
                }
            }
    println()
    val cores = Runtime.getRuntime().availableProcessors()
    val assigner = AtomicInteger(0)
    Observable.range(1, 10)
            .groupBy { assigner.incrementAndGet() % cores }
            .flatMap {
                it.observeOn(Schedulers.io()).map {
                    intenseCalculation(it)
                }
            }.apply {
                subscribe { println("subscribe: o2: $it ${ Thread.currentThread().name }") }.let {
                    sleep(5000)
                    it.dispose()
                }
            }
}
//flatMap()

fun unsubscribeOn() {
    Observable.interval(1, TimeUnit.SECONDS)
            .map {
                println("map: o1: ${ Thread.currentThread().name }")
                it
            }
            .doOnDispose {
                println("doOnDispose: o1: ${ Thread.currentThread().name }")
            }
            .unsubscribeOn(Schedulers.newThread())
            .subscribe {
                println("subscribe: o1: $it ${ Thread.currentThread().name }")
            }.let {
                sleep(5000)
                it.dispose()
            }
}
unsubscribeOn()