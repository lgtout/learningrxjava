import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.gojuno.koptional.toOptional
import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import java.util.concurrent.TimeUnit

//fun zip() {
//    val o1 = Observable.interval(2, 1, TimeUnit.SECONDS).map {
//        if (it == 2L) throw RuntimeException("Boom! 1")
//        else it.toString()
//    }.onErrorReturn { "" }
//            .doOnError { println(it) }
//            .doOnComplete { println("onComplete 1") }
//    val o2 = Observable.interval(1, TimeUnit.SECONDS)
//            .map { if (it == 2L) throw RuntimeException("Boom! 2") else it }
//            .doOnComplete { println("onComplete 2") }
//    o1.zipWith(o2)
//            .doOnComplete { println("onComplete 3") }
//            .onErrorReturn {
//                Pair("x", 10)
//            }
//            .subscribe(
//                    { println(it) },
//                    Throwable::printStackTrace,
//                    { println("complete") })
//    Thread.sleep(6000)
//}
//zip()

interface C

data class A(var b: String? = null) : C {
    val id = Companion.id
    companion object {
        var id = 0
            get() = field++
    }
}

class B: C

fun f() {
    val a2 = A()
    val a3 = A()
    val o1 = Observable.error<Optional<A>>({ RuntimeException("Boom!") })
            .onErrorReturnItem(None)
    val o2 = Observable.defer {
        Thread.sleep(2000)
        Observable.just("b")
    }.delay(1, TimeUnit.SECONDS).map<Optional<A>> {
        a2.apply { b = it }.toOptional()
    }
    val o3 = Observable.defer {
        Thread.sleep(2000)
        Observable.just("c")
    }.delay(2, TimeUnit.SECONDS).map<Optional<A>> {
        a3.apply { b = it }.toOptional()
    }
    val o4 = Observable.just<Optional<A>>(A("d").toOptional())
    val o5 = Observable.just<Optional<A>>(A("e").toOptional())
    Observable.zip(listOf(o1, o2, o3, o4, o5),
            {
                it.filterIsInstance<Optional<A>>().map {
                   when (it) {
                       None -> ""
                       is Some -> it.value.b
                   }
                }.reduce { acc, s -> acc + s }.let {
                    A(it).toOptional()
                }
    }).subscribe({ println(it) }, { println(it.toString()) }, { println("complete") })
    Thread.sleep(3000)
}
f()


fun f2() {
    Observable.merge(listOf(Observable.just<Int?>(1,2,3).delay(1, TimeUnit.SECONDS),
            Observable.error<Int?>(RuntimeException("Boom!")).onErrorReturnItem(null)))
            .subscribe({ println(it) }, { println(it.toString()) }, { println("complete") })
}
//f2()

fun f3() {
    Observable.just<Optional<Int>>(None)
            .subscribe({ println(it) }, { println(it.toString()) }, { println("complete") })
}
//f3()

fun f4() {
    Observable.zip(listOf(Observable.just(1,2,3), Observable.just(4,5,8)), {
        it.map { it as Int}.let {
            it[0] + it[1]
        }
    }).subscribe({ println(it) }, { println(it.toString()) }, { println("complete") })
}
//f4()