import rx.Observable
import rx.Subscriber
import rx.lang.kotlin.*

fun main(args: Array<String>) {
    val obs = observable<String> {
        it.onNext("Alles in Butter")
        it.onCompleted()
    }
    val extendedObs = obs.map {
        it + "!"
    }
    extendedObs.subscribe() {
        println(it)
    }
    val hashObs = obs.map {
        it.hashCode()
    }
    hashObs.subscribe() {
        println("#$it")
    }
}