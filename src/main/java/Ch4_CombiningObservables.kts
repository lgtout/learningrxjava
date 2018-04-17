import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask

fun merge() {
    Observable.merge(
            Observable.just(1,2,3),
            Observable.just(4,5,6))
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    Observable.merge(
            Observable.just(1,2,3),
            Observable.just(4,5,6))
            .subscribeOn(Schedulers.io())
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    Thread.sleep(100)
    println()
    Observable.just(1,2,3).mergeWith(
            Observable.just(4,5,6))
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    val d = Observable.merge(
            Observable.interval(1, TimeUnit.SECONDS).map { "a $it" },
            Observable.interval(2, TimeUnit.SECONDS).map { "b $it" })
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    Thread.sleep(4000)
    d.dispose()
    println()
    Observable.mergeArray(
                    Observable.just(1,2,3),
                    Observable.just(4,5,6),
                    Observable.just(4,5,6),
                    Observable.just(4,5,6),
                    Observable.just(4,5,6))
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    Observable.merge(
            listOf(Observable.just(1,2,3),
            Observable.just(4,5,6),
            Observable.just(4,5,6),
            Observable.just(4,5,6),
            Observable.just(4,5,6)))
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
}
//merge()

fun flatMap() {
    Observable.just("abcde", "fghij").flatMap {
        Observable.fromIterable(it.toList())
    }.subscribe({ println(it) },
            { Thread.sleep(100); it.printStackTrace() },
            { println("complete") })
    println()
    Observable.just("abcde", "fghij", "zzz").flatMap {
        if (it.length == 3) Observable.just<Char>('x')
        else Observable.fromIterable(it.toList())
    }.subscribe({ println(it) },
            { Thread.sleep(100); it.printStackTrace() },
            { println("complete") })
    println()
    Observable.just("abcde", "fghij").flatMap({
        Observable.fromIterable(it.toList())
    }, { t1: String, t2: Char ->
        Pair(t1, t2)
    }).subscribe({ println(it) },
            { Thread.sleep(100); it.printStackTrace() },
            { println("complete") })
    println()
    Observable.just("abcde", "fghij").flatMapIterable {
        it.toList()
    }.subscribe({ println(it) },
            { Thread.sleep(100); it.printStackTrace() },
            { println("complete") })
    println()
    Observable.just("abcde", "fghij")
            .flatMapSingle { Single.just(it.length) }
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    Observable.just("abcde", "fghij")
            .flatMapMaybe { Maybe.just(it.length) }
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    // Prints nothing - never completes or emits.
    println("flatMapMaybe")
    Observable.just("abcde", "fghij")
            .flatMapMaybe { Maybe.never<String>() }
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    println("flatMapCompletable")
    // Not sure why this doesn't call onComplete
    Observable.just("abcde", "fghij")
            .flatMapCompletable {
                Completable.create {
                    emitter ->
                    Timer().schedule(timerTask {
                        emitter.onComplete()
                    }, 1000)
                }
            }
            .subscribe({ Thread.sleep(100); },
                    { println("complete") })
    println()
}
//flatMap()

fun concat() {
    Observable.concat(Observable.just(1,2), Observable.just(3,4))
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    Observable.just(1,2).concatWith(Observable.just(3,4))
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    Observable.concatArray(Observable.just(1,2),
            Observable.just(3,4))
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    Observable.concat(listOf(Observable.just(1,2),
            Observable.just(3,4)))
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    Observable.just(1,2).concatMapIterable {
        listOf(it, it, it)
    }.subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    // Doesn't work as expected :(
    val o1 = Observable.range(0, 4).zipWith(Observable.interval(0, 500, TimeUnit.MILLISECONDS)) { i, _ -> i }
            .doOnNext { println(it) }
            .doOnSubscribe { println("subscribed to o1")}
    val o2 = Observable.just(1,2)
            .doOnNext { println(it) }
            .doOnSubscribe { println("subscribed to o2")}
            .publish()
    o2.connect()
    Observable.just(1, 2).concatMapEager {
        if (it == 1) o2 else o1
    }.subscribe({ println(it) },
            { Thread.sleep(100); it.printStackTrace() },
            { println("complete") })
    Thread.sleep(5000)
}
//concat()

fun concatMap() {
    Observable.just("abcde","fghijk").concatMap {
        t -> Observable.fromIterable(t.toList())
    }.subscribe({ println(it) },
            { Thread.sleep(100); it.printStackTrace() },
            { println("complete") })
}
//concatMap()

fun amb() {
    Observable.amb(listOf(
            Observable.interval(2, TimeUnit.SECONDS).take(2).map { "o1 $it" },
            Observable.interval(1, TimeUnit.SECONDS).take(2).map { "o2 $it" }))
    .subscribe({ println(it) },
            { Thread.sleep(100); it.printStackTrace() },
            { println("complete") })
    Thread.sleep(5000)
    println()
    Observable.interval(2, TimeUnit.SECONDS).take(2).map { "o1 $it" }
            .ambWith(Observable.interval(1, TimeUnit.SECONDS).take(2).map { "o2 $it" })
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    Thread.sleep(5000)
    println()
    Observable.ambArray(
            Observable.interval(2, TimeUnit.SECONDS).take(2).map { "o1 $it" },
            Observable.interval(1, TimeUnit.SECONDS).take(2).map { "o2 $it" })
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    Thread.sleep(5000)
    println()
}
//amb()

fun zip() {
    Observable.zip(Observable.just('a','b','c'),
            Observable.just(1,2,3,5,6),
            BiFunction { a: Char, b: Int -> "$a-$b"})
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    Observable.just('a','b','c').zipWith(Observable.just(1,2,3,5,6),
            BiFunction { a: Char, b: Int -> "$a-$b"})
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    // Doesn't behave as expected
    Observable.zipIterable(listOf(Observable.just('a','b','c'),
            Observable.just(1,2,3,5,6).doOnNext({ it -> println("o2: $it") })
                    .map { if (it == 2) throw RuntimeException("Boom!") else it }),
            { a -> "${a[0]}-${a[1]}" }, true, 1)
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
}
//zip()

fun combineLatest() {
    Observable.combineLatest(Observable.interval(1, TimeUnit.SECONDS).map { "o1: $it" },
            Observable.interval(300, TimeUnit.MILLISECONDS).map { "o2: $it " },
            BiFunction { a: String, b: String -> "$a $b" })
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    Thread.sleep(3000)
}
//combineLatest()

fun withLatestFrom() {
    Observable.interval(1, TimeUnit.SECONDS).map { "o1: $it" }.withLatestFrom(
            Observable.interval(300, TimeUnit.MILLISECONDS).map { "o2: $it " },
            BiFunction { a: String, b: String -> "$a $b" })
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") }).let {
                Thread.sleep(3000)
                it.dispose()
            }
    println()
    Observable.interval(1, TimeUnit.SECONDS).map { "o1: $it" }.withLatestFrom(
            listOf(Observable.interval(300, TimeUnit.MILLISECONDS).map { "o2: $it " },
                    Observable.interval(150, TimeUnit.MILLISECONDS).map { "o3: $it " }),
            { b: Array<*> -> "${b[0]} ${b[1]} ${b[2]}" })
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    Thread.sleep(3000)
}
//withLatestFrom()

fun groupBy() {
    Observable.just("abc", "aac", "cda", "dca", "cbb")
            .groupBy { it.first() }
            .flatMapSingle { g ->
                g.toList().map {
                    "${g.key}: $it"
                }
            }
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
    println()
    Observable.just("abc", "aac", "cda", "dca", "cbb")
            .groupBy { it.first() }
            .flatMap { g ->
                g.toList().map {
                    "${g.key}: $it"
                }.toObservable()
            }
            .subscribe({ println(it) },
                    { Thread.sleep(100); it.printStackTrace() },
                    { println("complete") })
}
groupBy()