import io.reactivex.Observable
import io.reactivex.subjects.*
import java.util.*
import java.util.concurrent.TimeUnit

// Multicasting

fun connectableObservable() {
    var o = Observable.range(1,5).publish()
    o.subscribe { println("o1: $it") }
    o.subscribe { println("o2: $it") }
    o.connect()
    println()

    val r = Random()
    o = Observable.range(1,5).publish()
    val o2 = o.map { r.nextInt(100) }
    o2.subscribe { println("o1: $it") }
    o2.subscribe { println("o2: $it") }
    o.connect()
    println()

    o = Observable.range(1,5).map { r.nextInt(100) }.publish()
    o.subscribe { println("o1: $it") }
    o.subscribe { println("o2: $it") }
    o.connect()
    o.subscribe ({ println("o3: $it") },
            Throwable::printStackTrace,
            { println("complete") })
}
//connectableObservable()

fun autoConnect() {
    var o = Observable.range(1,5).publish().autoConnect()
    o.subscribe { println("o1: $it") }
    o.subscribe { println("o2: $it") }
    println()

    o = Observable.range(1,5).publish().autoConnect(2)
    o.subscribe { println("o1: $it") }
    o.subscribe { println("o2: $it") }
    println()

    println("no emissions expected")
    o = Observable.range(1,5).publish().autoConnect(3)
    o.subscribe { println("o1: $it") }
    o.subscribe { println("o2: $it") }
    println()

    o = Observable.range(1,5).publish().autoConnect(2)
    o.subscribe { println("o1: $it") }
    o.subscribe { println("o2: $it") }
    o.subscribe { println("o3: $it") }
    println()

    var o2 = Observable.interval(300, TimeUnit.MILLISECONDS).publish().autoConnect(2)
    var d1 = o2.subscribe { println("o1: $it") }
    var d2 = o2.subscribe { println("o2: $it") }
    Thread.sleep(1000)
    var d3 = o2.subscribe { println("o3: $it") }
    Thread.sleep(1000)
    println()
    listOf(d1,d2,d3).forEach { it.dispose() }

    o = Observable.range(1,5).publish().autoConnect()
    o.subscribe { println("o1: $it") }
    o.subscribe { println("o2: $it") }
    println()

    println("no emissions expected")
    o = Observable.range(1,5).publish().autoConnect(0)
    o.subscribe { println("o1: $it") }
    o.subscribe { println("o2: $it") }
    println()

    o2 = Observable.interval(300, TimeUnit.MILLISECONDS).publish().autoConnect(0)
    Thread.sleep(1000)
    o2.subscribe { println("o1: $it") }
    o2.subscribe { println("o2: $it") }
    Thread.sleep(1000)
    println()
}
//autoConnect()

fun refCount() {
    Observable.interval(1, TimeUnit.SECONDS).publish().refCount().apply {
        val d1 = take(5).subscribe { println("o1: $it") }
        Thread.sleep(3000)
        val d2 = take(2).subscribe { println("o2: $it") }
        Thread.sleep(3000)
        val d3 = subscribe { println("o3: $it") }
        Thread.sleep(3000)
        listOf(d1, d2, d3).forEach { it.dispose() }
        println()
    }

    val o = Observable.interval(1, TimeUnit.SECONDS).share()
    val d1 = o.take(5).subscribe { println("o1: $it") }
    Thread.sleep(3000)
    val d2 = o.take(2).subscribe { println("o2: $it") }
    Thread.sleep(3000)
    val d3 = o.subscribe { println("o3: $it") }
    Thread.sleep(3000)
    listOf(d1, d2, d3).forEach { it.dispose() }
    println()
}
//refCount()

fun replay() {
    Observable.interval(300, TimeUnit.MILLISECONDS).replay().autoConnect().apply {
        val d1 = subscribe { println("o1: $it") }
        Thread.sleep(1000)
        val d2 = subscribe { println("o2: $it") }
        Thread.sleep(1000)
        listOf(d1, d2).forEach { it.dispose() }
    }
    println()
    Observable.interval(250, TimeUnit.MILLISECONDS).replay(2).autoConnect().apply {
        Thread.sleep(1000)
        val d1 = subscribe { println("o1: $it") }
        Thread.sleep(1000)
        val d2 = subscribe { println("o2: $it") }
        Thread.sleep(1000)
        listOf(d1, d2).forEach { it.dispose() }
    }
    println()
    Observable.range(1,5).replay(2).autoConnect().apply {
        val d1 = subscribe { println("o1: $it") }
        val d2 = subscribe { println("o2: $it") }
        listOf(d1, d2).forEach { it.dispose() }
    }
    println()
    Observable.range(1,5).replay(2).refCount().apply {
        val d1 = subscribe { println("o1: $it") }
        val d2 = subscribe { println("o2: $it") }
        listOf(d1, d2).forEach { it.dispose() }
    }
    println()
    Observable.interval(200, TimeUnit.MILLISECONDS)
            .replay(1000, TimeUnit.MILLISECONDS).autoConnect().apply {
                val d1 = subscribe { println("o1: $it") }
                Thread.sleep(2000)
                val d2 = subscribe { println("o2: $it") }
                Thread.sleep(1000)
                listOf(d1, d2).forEach { it.dispose() }
    }
    println()
    Observable.interval(200, TimeUnit.MILLISECONDS)
            .replay(1000, 2, TimeUnit.MILLISECONDS).autoConnect().apply {
                val d1 = subscribe { println("o1: $it") }
                Thread.sleep(2000)
                val d2 = subscribe { println("o2: $it") }
                Thread.sleep(1000)
                listOf(d1, d2).forEach { it.dispose() }
            }
    println()
}
//replay()

fun cache() {
    Observable.interval(200, TimeUnit.MILLISECONDS).cache().apply {
        val d1 = subscribe { println("o1: $it") }
        Thread.sleep(2000)
        val d2 = subscribe { println("o2: $it") }
        Thread.sleep(1000)
        listOf(d1, d2).forEach { it.dispose() }
    }
}
//cache()

fun publishSubject() {
    PublishSubject.create<Int>().apply {
        subscribe { println("o1: $it") }
        onNext(1)
        onNext(2)
        onNext(3)
        onComplete()
    }
    println()
    PublishSubject.create<String>().let { ps ->
        ps.subscribe { println(it) }
        val d1 = Observable.interval(200, TimeUnit.MILLISECONDS)
                .map { "s1: $it"}.subscribe { it -> ps.onNext(it) }
        val d2 = Observable.interval(300, TimeUnit.MILLISECONDS)
                .map { "s2: $it"}.subscribe { it -> ps.onNext(it) }
        Thread.sleep(2000)
        listOf(d1, d2).forEach { it.dispose() }
    }
    println()
}
//publishSubject()

fun behaviorSubject() {
    BehaviorSubject.create<Int>().apply {
        subscribe { println("o1: $it") }
        onNext(1)
        onNext(2)
        subscribe { println("o2: $it") }
        onNext(3)
        onComplete()
    }
    println()
}
//behaviorSubject()

fun replaySubject() {
    ReplaySubject.create<Int>().apply {
        onNext(1)
        onNext(2)
        onNext(3)
        onComplete()
        subscribe { println("o1: $it") }
    }
    println()
}
//replaySubject()

fun asyncSubject() {
    AsyncSubject.create<Int>().apply {
        subscribe { println("o1: $it") }
        onNext(1)
        onNext(2)
        onNext(3)
        onComplete()
    }
    println()
}
//asyncSubject()

fun unicastSubject() {
    UnicastSubject.create<Int>().apply {
        onNext(1)
        onNext(2)
        onNext(3)
        onComplete()
        subscribe { println("o1: $it") }
        // Throws exception: only one observer allowed.
//        subscribe { println("o2: $it") }
    }
    println()
    UnicastSubject.create<Int>().apply {
        publish().autoConnect().apply {
            subscribe { println("o1: $it") }
            subscribe { println("o2: $it") }
        }
        onNext(1)
        onNext(2)
        onNext(3)
        onComplete()
    }
    println()
}
unicastSubject()


