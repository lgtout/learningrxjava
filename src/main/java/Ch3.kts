@file:Suppress("FunctionName")

import io.reactivex.Observable
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

}

ex_21()