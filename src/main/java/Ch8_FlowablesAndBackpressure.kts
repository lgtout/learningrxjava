import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscriber
import org.reactivestreams.Subscription
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

fun interval() {
    Flowable.interval(0, 1, TimeUnit.MILLISECONDS)
            .doOnNext { println("emitting: $it") }
            .observeOn(Schedulers.computation())
            .map { Thread.sleep(2000); it }
            .subscribe({ println("receiving: $it") }, Throwable::printStackTrace)
            .apply {
                Thread.sleep(3000)
                dispose()
            }
    println()
}
//interval()

fun subscriber() {
    val r = Random(1)
    Flowable.range(0, 200)
            .doOnNext { println("emitting: $it") }
            .observeOn(Schedulers.computation())
            .map { Thread.sleep(r.nextInt(200).toLong()); it }
            .subscribe({ println("receiving: $it") }, Throwable::printStackTrace)
            .apply {
                Thread.sleep(20000)
                dispose()
            }
    println()
    Flowable.range(0, 200)
            .doOnNext { println("emitting: $it") }
            .observeOn(Schedulers.io())
            .map { Thread.sleep(r.nextInt(200).toLong()); it }
            .subscribe(object : Subscriber<Int> {
                override fun onComplete() {
                    println("done!")
                }

                override fun onSubscribe(s: Subscription?) {
                    s?.request(Long.MAX_VALUE)
                }

                override fun onNext(t: Int?) {
                    Thread.sleep(50)
                    println("received $t")
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                }
            })
    println()
    Flowable.range(0, 200)
            .doOnNext { println("emitting: $it") }
            .observeOn(Schedulers.io())
            .map { Thread.sleep(r.nextInt(200).toLong()); it }
            .subscribe(object : Subscriber<Int> {
                var s: Subscription? = null
                val count = AtomicInteger(0)
                override fun onComplete() {
                    println("done!")
                }

                override fun onSubscribe(s: Subscription?) {
                    this.s = s
                    println("requesting 40")
                    s?.request(40)
                }

                override fun onNext(t: Int?) {
                    Thread.sleep(50)
                    if (count.getAndIncrement() % 20 == 0 &&
                            count.get() >= 40) {
                        println("requesting 20 more")
                        s?.request(20)
                    }
                    println("received $t")
                }

                override fun onError(t: Throwable?) {
                    t?.printStackTrace()
                }
            })
    println()
}
//subscriber()

fun create() {
    val source = Flowable.create<Int>({ emitter ->
        emitter.isCancelled
    }, BackpressureStrategy.BUFFER)
}
create()
