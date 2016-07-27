package net.taobits.grokking_rxjava.part1

import rx.*
import rx.lang.kotlin.*

// See see http://blog.danlew.net/2014/09/15/grokking-rxjava-part-1/

fun main(args: Array<String>) {
    val obs: Observable<String> = observable<String> { // Observable for String
        it.onNext("Alles in Butter")
        it.onCompleted()
    }
    val extendedObs: Observable<String> = obs.map { // Observable adding an exclamation mark
        it + "!"
    }
    extendedObs.subscribe() { // Printing including exclamation marks
        println(it)
    }
    val hashObs: Observable<Int> = obs.map { // Observable hashing
        it.hashCode()
    }
    hashObs.subscribe() { // Printing hashs
        println("#$it")
    }
}