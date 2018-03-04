@file:Suppress("FunctionName")

import com.google.common.collect.ImmutableList
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit
import kotlin.comparisons.naturalOrder
// filter

fun ex_1() {
    Observable.range(0,100).filter { it % 5 == 0 }
            .subscribe(::println)
}

//ex_1()

// take

// Take a number of emissions
fun ex_2() {
    Observable.range(0,100).take(5).subscribe(
            { println("observable 1 $it") },
            Throwable::printStackTrace,
            { println("complete") })
    Observable.range(0,1).take(5).subscribe(
            { println("observable 2 $it") },
            Throwable::printStackTrace,
            { println("complete") })
}

//ex_2()

// Take emissions within a time window
fun ex_3() {
    Observable.interval(300, TimeUnit.MILLISECONDS)
            .take(3, TimeUnit.SECONDS).subscribe(
                    { println("observable 1 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    Thread.sleep(4000)
    Observable.range(0,100).takeLast(5).subscribe(
            { println("observable 2 $it") },
            Throwable::printStackTrace,
            { println("complete") })
}

//ex_3()

// skip

// Skip first n emissions, or emissions within a time window
fun ex_4() {
    val d = Observable.interval(330, TimeUnit.MILLISECONDS)
            .skip(3, TimeUnit.SECONDS).subscribe(
                    { println("observable 1 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    Thread.sleep(5000)
    d.dispose()
    println()
    Observable.interval(330, TimeUnit.MILLISECONDS)
            .skip(6).subscribe(
                    { println("observable 2 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    Thread.sleep(5000)
}

//ex_4()

// takeWhile
fun ex_5() {
    Observable.range(0, 10).takeWhile { it <= 5}
            .subscribe(
                    { println("observable $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_5()

// skipWhile, takeUntil, skipUntil
fun ex_6() {
    Observable.range(0, 10).skipWhile { it <= 5}
            .subscribe(
                    { println("observable 1 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.range(0, 10).takeUntil { it == 5}
            .subscribe(
                    { println("observable 2 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.interval(1, TimeUnit.SECONDS)
            .skipUntil(Observable.timer(4, TimeUnit.SECONDS))
            .subscribe(
                    { println("observable 3 $it") },
                    Throwable::printStackTrace,
                    { println("complete") }).let {
                Thread.sleep(6000)
                it.dispose()
            }
    println()
    Observable.interval(1, TimeUnit.SECONDS)
            .takeUntil(Observable.timer(4, TimeUnit.SECONDS))
            .subscribe(
                    { println("observable 4 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    Thread.sleep(6000)
}

//ex_6()

// distinct
fun ex_7() {
    Observable.fromIterable(listOf(1,1,1,1,1,2,2,2,2,2,3,3,3,3))
            .distinct()
            .subscribe(
                    { println("observable $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_7()

// distinct
fun ex_8() {
    Observable.fromIterable(listOf("ab","ac","ba","bc","c"))
            .distinct { it.first() }
            .subscribe(
                    { println("observable $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_8()

// distinctUntilChanged
fun ex_9() {
    Observable.fromIterable(listOf(1, 1, 1, 2, 1, 1, 2, 2, 3, 2, 2, 3, 3, 3))
            .distinctUntilChanged()
            .subscribe(
                    { println("observable $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_9()

// distinctUntilChanged with comparer or key selector
fun ex_10() {
    Observable.fromIterable(listOf("ab","ac","ba","ad","bc","c"))
            .distinctUntilChanged { previous, current ->
                previous.first() != current.first() }
            .subscribe(
                    { println("observable 1 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println("")
    Observable.fromIterable(listOf("ab","ac","ba","ad","bc","c"))
            .distinctUntilChanged(
                    io.reactivex.functions.Function<String, Char>{ it.first() })
            .subscribe(
                    { println("observable 2 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_10()

// elementAt, elementAtOrError, singleElement, firstElement, lastElement
fun ex_11a() {
    Observable.just("a","b","c")
            .elementAt(1)
            .subscribe(
                    { println("o1 $it") },
                    Throwable::printStackTrace,
                    { println("observable 1 complete") })
    println()
    Observable.just("a","b","c")
            .elementAt(4)
            .subscribe(
                    { println("o2 $it") },
                    Throwable::printStackTrace,
                    { println("observable 2 complete") })
    println()
    Observable.just("a","b","c")
            .elementAtOrError(4)
            .subscribe(
                    { println("o3 $it") },
                    Throwable::printStackTrace)
    println()
    Observable.just("a","b","c")
            .singleElement()
            .subscribe(
                    { println("o4 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    // TODO singleElement, firstElement, lastElement
}

//ex_11a()

// singleElement, firstElement, lastElement
fun ex_11b() {
    // TODO singleElement, firstElement, lastElement
    Observable.just("a","b","c")
            .firstElement()
            .subscribe(
                    { println("o1 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just("a","b","c")
            .lastElement()
            .subscribe(
                    { println("o2 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just("a")
            .singleElement()
            .subscribe(
                    { println("o3 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.empty<Int>()
            .singleElement()
            .subscribe(
                    { println("o4 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just("a","b","c")
            .singleElement()
            .subscribe(
                    { println("o5 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_11b()

// Transforming operators

// map
fun ex_12() {
    Observable.just("aaaa","bb","ccccccccc")
            .map { it.length }
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_12()

// cast
fun ex_13() {
    open class A
    class B : A()
    Observable.just(B(), B())
            .cast(A::class.java)
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_13()

// startWith, startWithArray
fun ex_14() {
    Observable.just(1,2,3)
            .startWith(0)
            .subscribe(
                    { println("o1 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just(1,2,3)
            .startWithArray(-2,-1,0)
            .subscribe(
                    { println("o2 $it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_14()

// defaultIfEmpty
fun ex_15() {
    Observable.empty<Int>()
            .defaultIfEmpty(0)
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just(1,2,3)
            .defaultIfEmpty(0)
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just(1,2,3)
            .filter { it > 4}
            .defaultIfEmpty(0)
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_15()

// switchIfEmpty
fun ex_16() {
    Observable.empty<Int>()
            .switchIfEmpty(Observable.just(10,11,12))
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just(1,2,3)
            .switchIfEmpty(Observable.just(10,11,12))
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_16()

// sorted
fun ex_17() {
    Observable.just(3,2,1)
            .sorted()
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just(3,1,2)
            .sorted { o1, o2 -> o1.compareTo(o2) }
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just(3,1,2)
            .sorted(naturalOrder())
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_17()

// delay
fun ex_18() {
    println("starting")
    Observable.just(1,2,3)
            .delay(4, TimeUnit.SECONDS)
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") }).let {
        Thread.sleep(5000)
        it.dispose()
    }
    println("starting")
    Observable.just(1,2,3)
            .delay { Observable.just(1)
                    .delay(4, TimeUnit.SECONDS) }
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") }).let {
        Thread.sleep(5000)
    }
}

//ex_18()

// repeat
fun ex_19() {
    Observable.just(1,2,3)
            .repeat(3)
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just(1,2,3)
            .delay(2, TimeUnit.SECONDS)
            .repeat(3)
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    Thread.sleep(10000)
}

//ex_19()

// scan
fun ex_20() {
    Observable.just(1,2,3)
            .scan { a, b -> a + b }
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just(1,2,3)
            .scan(-1) { a, b -> a + b }
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
    println()
    Observable.just(1,2,3)
            .scan(-1) { a, b -> a + b }
            .skip(1)
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace,
                    { println("complete") })
}

//ex_20()

// Reducing operators

// count
fun ex_21() {
    Observable.just(1,1,1,1,1).count()
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace)
}

//ex_21()

fun scan() {
    Observable.just(1,1,1,1,1).scan({ acc, i -> acc + i })
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace)
    println()
    Observable.just(1,1,1,1,1).scan(1, { acc, i -> acc + i })
            .subscribe(
                    { println("$it") },
                    Throwable::printStackTrace)
}

//scan()

fun reduce() {
    Observable.just(1,1,1,1,1).reduce { acc, it -> acc + it}.let {
        it.subscribe(
                { println("o1 $it") },
                Throwable::printStackTrace)
    }
}

//reduce()

fun all() {
    Observable.just(1,1,1,1,1).all { it < 2 }
            .subscribe({ println("o1 $it") },
                    Throwable::printStackTrace)
    Observable.just(1,1,1,2,1).all { it < 2 }
            .subscribe({ println("o2 $it") },
                    Throwable::printStackTrace)
}

//all()

fun any() {
    Observable.just(1,1,1,2,2).any { it > 1 }
            .subscribe({ println("o1 $it") },
                    Throwable::printStackTrace)
    Observable.just(1,1,1,2,2).any { it > 2 }
            .subscribe({ println("o2 $it") },
                    Throwable::printStackTrace)
}

//any()

fun contains() {
    Observable.just(1,1,1,2,2).contains(2)
            .subscribe({ println("o1 $it") },
                    Throwable::printStackTrace)
    Observable.just(1,1,1,2,2).contains(3)
            .subscribe({ println("o2 $it") },
                    Throwable::printStackTrace)
}

//contains()

fun toList() {
    Observable.just(1,1,1,2,2).toList()
            .subscribe({ println("o1 $it") },
                    Throwable::printStackTrace)
    Observable.just(1,1,1,2).toList(4)
            .subscribe({ println("o2 $it") },
                    Throwable::printStackTrace)
    Observable.just(1,1,1,2,2,3).toList { mutableSetOf<Int>() }
            .subscribe({ println("o3 $it") },
                    Throwable::printStackTrace)
}
//toList()

fun toSortedList() {
    Observable.just(4,3,1,2,3,1).toSortedList()
            .subscribe({ println("o1 $it") },
                    Throwable::printStackTrace)
    Observable.just("adsf", "xx","x").toSortedList {
        o1, o2 -> o1.length.compareTo(o2.length) }
            .subscribe({ println("o2 $it") },
                    Throwable::printStackTrace)
}
//toSortedList()

fun toMap() {
    Observable.just("adsf", "xx","x", "c", "bb").toMap { it.first() }
            .subscribe({ println("o1 $it") },
                    Throwable::printStackTrace)
    println()
    Observable.just("adsf", "xx","x", "c", "bb").toMap({ it.first() }, { it.length })
            .subscribe({ println("o2 $it") },
                    Throwable::printStackTrace)
    println()
    Observable.just("adsf", "xx","x", "c", "bb").toMap({ it.first() }, { it.length }, ::HashMap)
            .subscribe({ println("o3 $it") },
                    Throwable::printStackTrace)
}
//toMap()

fun toMultiMap() {
    Observable.just("adsf", "xx","x", "c", "bb").toMultimap { it.first() }
            .subscribe({ println("o1 $it") },
                    Throwable::printStackTrace)
}
//toMultiMap()

fun collect() {
    Observable.just("adsf", "xx","x", "xx", "c", "bb").collect(
            { mutableSetOf<String>() }, { t1, t2 -> t1.add(t2) })
            .subscribe({ println("o1 $it") }, Throwable::printStackTrace)
    println()
    Observable.just("adsf", "xx","x", "xx", "c", "bb").collect(
            { ImmutableList.Builder<String>() }, { t1, t2 -> t1.add(t2) })
            .map { it.build() }
            .subscribe({ println("o2 $it") }, Throwable::printStackTrace)
}
//collect()

fun errorReturn() {
    Observable.just(1,0,1).map { 10 / it }
            .onErrorReturn { _: Throwable -> -1 }
            .subscribe({ println("o1 $it") }, Throwable::printStackTrace)
}
//errorReturn()

fun errorReturnItem() {
    Observable.just(1,0,1).map { 10 / it }
            .onErrorReturnItem(-1)
            .subscribe({ println("o1 $it") }, Throwable::printStackTrace)
}
//errorReturnItem()

// Keep emitting after exception
fun _catchExceptionInOperator() {
    Observable.just(1,0,1).map {
        try {
            10 / it
        } catch (e: Exception) {
            -1
        }
    }.onErrorReturnItem(-1)
            .subscribe({ println("o1 $it") }, Throwable::printStackTrace)
}
//_catchExceptionInOperator()

fun onErrorResumeNext() {
    Observable.just(1,0,1).map { 10 / it }
            .onErrorResumeNext(Observable.just(1,1,1))
            .subscribe({ println("o1 $it") }, Throwable::printStackTrace)
    println()
    Observable.just(1,0,1).map { 10 / it }
            .onErrorResumeNext(Observable.empty())
            .subscribe({ println("o2 $it") }, Throwable::printStackTrace)
    println()
    Observable.just(1,0,1).map { 10 / it }
            .onErrorResumeNext({ _: Throwable -> Observable.just(-1).repeat(3) })
            .subscribe({ println("o3 $it") }, Throwable::printStackTrace)
}
//onErrorResumeNext()

fun retry() {
    var count = 0
    Observable.just(1,2,3,4).map {
        if (it == 2 && count < 2) {
            count += 1
            throw RuntimeException("Boom!")
        }
        else it
    }.retry().subscribe({ println("o1 $it") }, Throwable::printStackTrace)
    count = 0
    Thread.sleep(1000)
    println()
    Observable.just(1,2,3,4).map {
        if (it == 2 && count < 3) {
            count += 1
            throw RuntimeException("Boom!")
        }
        else it
    }.retry(2).subscribe({ println("o2 $it") },
            { e ->
                Thread.sleep(100)
                e.printStackTrace()
            })
    Thread.sleep(1000)
    println()
    count = 0
    Observable.just(1,2,3,4).map {
        if (it == 2 && count < 3) {
            count += 1
            throw RuntimeException("Boom!")
        }
        else it
    }.retry({ retryCount, _: Throwable -> retryCount < 4 })
            .subscribe({ println("o3 $it") },
                    { e ->
                        Thread.sleep(100)
                        e.printStackTrace()
                    })
    println()
    count = 0
    Observable.just(1,2,3,4).map {
        if (it == 2 && count < 3) {
            count += 1
            throw RuntimeException("Boom!")
        }
        else it
    }.retryUntil({ count > 4 })
            .subscribe({ println("o3 $it") },
                    { e ->
                        Thread.sleep(100)
                        e.printStackTrace()
                    })
}
//retry()

fun doOnNext() {
    Observable.just(1,2)
            .doOnNext { println("doOnNext: $it") }
            .subscribe { println(it) }
    println()
    Observable.just(1,2)
            .doOnNext { println("doOnNext: $it") }
            .doAfterNext { println("doAfterNext: $it") }
            .map { (it + 1).also {
                println("map $it")
            } }
            .map { (it + 1).also {
                println("map $it")
            } }
            .subscribe { println(it) }
}
//doOnNext()

fun doOnComplete() {
    Observable.just(1,2,3)
            .doOnComplete { println("doOnComplete 1") }
            .map { (it + 1).also {
                println("map $it")
            } }
            .doOnComplete { println("doOnComplete 2") }
            .switchMap {
                Observable.just(4,5,6)
            }
            .doOnComplete { println("doOnComplete 3") }
            .subscribe { println(it) }
}
//doOnComplete()

fun doOnError() {
    Observable.just(1,2,3)
            .doOnError { println("doOnError 1") }
            .concatWith(Observable.error<Int>(RuntimeException("Boom!")))
            .doOnError { println("doOnError 2") }
            .subscribe({ println(it) }, {
                Thread.sleep(100)
                it.printStackTrace() })
    println()
    Observable.just(1,2,3)
            .concatWith(Observable.error<Int>(RuntimeException("Boom!")))
            .doOnEach(object : Observer<Int> {
                override fun onComplete() {
                    println("onComplete")
                }

                override fun onSubscribe(d: Disposable) {
                    println("onSubscribe")
                }

                override fun onNext(t: Int) {
                    println("onNext $t")
                }

                override fun onError(e: Throwable) {
                    println("doOnError 1")
                }
            }).subscribe({ println(it) }, {
                Thread.sleep(100)
                it.printStackTrace() })
}
//doOnError()

