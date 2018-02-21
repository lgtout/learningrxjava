import io.reactivex.Observable
import io.reactivex.rxkotlin.toObservable
import java.util.concurrent.TimeUnit

/* Create an observable that emits 5 times. */
fun ex_1() {
    listOf(1,2,3,4,5).toObservable()
            .subscribe { println(it) }
}

ex_1()

/* Create an observable of 5 strings.  Convert
the strings to an observable of lengths of the
strings. */
fun ex_2() {
    listOf("a", "ab", "abc", "abcd", "abcde").toObservable()
            .map { it.length }.subscribe { println(it) }
}

ex_2()

/* Create an observable that emits at 1 second
intervals. */
fun ex_3() {
    Observable.interval(1, TimeUnit.SECONDS)
            .subscribe { println(it) }
    Thread.sleep(5000)
}

ex_3()

