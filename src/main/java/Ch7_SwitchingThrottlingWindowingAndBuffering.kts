import io.reactivex.Observable
import java.util.*
import java.util.concurrent.TimeUnit

fun fixedSizeBuffer() {
    Observable.range(1,64)
            .buffer(8)
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }
    println()
    Observable.range(1,64)
            .buffer(8, { linkedSetOf<Int>() } )
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }
    println()
    Observable.range(1,64)
            .buffer(8, 4, { linkedSetOf<Int>() } )
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }
    println()
    Observable.range(1,64)
            .buffer(8, 12, { linkedSetOf<Int>() } )
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }
    println()
    Observable.range(1,64)
            .buffer(8, 12, { linkedSetOf<Int>() } )
            .filter { it.size == 8 }
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }
}
//fixedSizeBuffer()

fun timeBasedBuffer() {
    Observable.interval(300, TimeUnit.MILLISECONDS)
            .buffer(700, TimeUnit.MILLISECONDS)
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }.apply {
                Thread.sleep(5000)
                dispose()
            }
    println()
    Observable.interval(300, TimeUnit.MILLISECONDS)
            .buffer(700, 300, TimeUnit.MILLISECONDS)
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }.apply {
                Thread.sleep(3000)
                dispose()
            }
    println()
    Observable.interval(300, TimeUnit.MILLISECONDS)
            .buffer(700, 1000, TimeUnit.MILLISECONDS)
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }.apply {
                Thread.sleep(5000)
                dispose()
            }
    println()
    Observable.interval(200, TimeUnit.MILLISECONDS)
            .buffer(700, TimeUnit.MILLISECONDS, 2)
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }.apply {
                Thread.sleep(5000)
                dispose()
            }
    println()
}
//timeBasedBuffer()

fun boundaryBasedBuffer() {
     Observable.interval(200, TimeUnit.MILLISECONDS)
            .buffer(Observable.interval(600, TimeUnit.MILLISECONDS))
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }.apply {
                Thread.sleep(5000)
                dispose()
            }
    println()
}
//boundaryBasedBuffer()

fun window() {
    Observable.interval(200, TimeUnit.MILLISECONDS)
            .window(3)
            .flatMapSingle { it.reduce("", { t , n ->
                t + (if (t == "") "" else "|") + n }) }
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }.apply {
                Thread.sleep(3000)
                dispose()
            }
    println()
    Observable.interval(200, TimeUnit.MILLISECONDS)
            .window(Observable.interval(600, TimeUnit.MILLISECONDS))
            .flatMap { it }
            .subscribe {
                println("subscribe: o2: $it ${
                Thread.currentThread().name }")
            }.apply {
                Thread.sleep(2000)
                dispose()
            }
    println()
    // skip 2
    Observable.interval(200, TimeUnit.MILLISECONDS)
            .window(2, 3)
            .flatMapSingle { it.reduce("", { t , n ->
                t + (if (t == "") "" else "|") + n }) }
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }.apply {
                Thread.sleep(3000)
                dispose()
            }
    println()
}
//window()

fun timeBasedWindow() {
    Observable.interval(200, TimeUnit.MILLISECONDS)
            .window(600, TimeUnit.MILLISECONDS)
            .flatMapSingle { it.reduce("", { t , n ->
                t + (if (t == "") "" else "|") + n }) }
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }.apply {
                Thread.sleep(3000)
                dispose()
            }
    println()
    Observable.interval(200, TimeUnit.MILLISECONDS)
            .window(600, TimeUnit.MILLISECONDS, 2)
            .flatMapSingle { it.reduce("", { t , n ->
                t + (if (t == "") "" else "|") + n }) }
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }.apply {
                Thread.sleep(3000)
                dispose()
            }
    println()
}
//timeBasedWindow()

fun boundaryBasedWindow() {
    Observable.interval(200, TimeUnit.MILLISECONDS)
            .window(Observable.interval(600, TimeUnit.MILLISECONDS))
            .flatMapSingle { t -> t.reduce("") { acc, i -> acc +
                    (if (acc.isNotEmpty()) "|" else "") + i } }
            .subscribe {
                println("subscribe: o1: $it ${
                Thread.currentThread().name }")
            }.apply {
                Thread.sleep(3000)
                dispose()
            }
    println()
}
//boundaryBasedWindow()

fun throttling() {
    fun o() = Observable.concat(
            Observable.interval(100, TimeUnit.MILLISECONDS)
                    .map { "o1 ${(it + 1) * 100}"}.take(10),
            Observable.interval(300, TimeUnit.MILLISECONDS)
                    .map { "o2 ${(it + 1) * 300}"}.take(3),
            Observable.interval(2000, TimeUnit.MILLISECONDS)
                    .map { "o3 ${(it + 1) * 2000}"}.take(3))
    o().subscribe {
        println(it)
    }.apply {
        Thread.sleep(6000)
        dispose()
    }
    println()
    o().throttleLast(1, TimeUnit.SECONDS)
            .subscribe {
                println(it)
            }.apply {
                Thread.sleep(8000)
                dispose()
            }
    println()
    o().throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                println(it)
            }.apply {
                Thread.sleep(8000)
                dispose()
            }
    println()
    o().throttleWithTimeout(1, TimeUnit.SECONDS)
            .subscribe {
                println(it)
            }.apply {
                Thread.sleep(8000)
                dispose()
            }
    println()
    o().throttleWithTimeout(2, TimeUnit.SECONDS)
            .subscribe {
                println(it)
            }.apply {
                Thread.sleep(8000)
                dispose()
            }
}
//throttling()

fun switching() {
    val r = Random(1)
    fun o() = Observable.range(1, 10).concatMap {
        Observable.just(it).delay(
            r.nextInt(3).toLong(), TimeUnit.SECONDS)
    }
    Observable.interval(5, TimeUnit.SECONDS)
            .switchMap {
                o().doOnDispose { println("disposing") }
            }.subscribe {
                println(it)
            }.apply {
                Thread.sleep(20000)
                dispose()
            }
}
switching()