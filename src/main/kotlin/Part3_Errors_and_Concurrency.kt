package net.taobits.grokking_rxjava.part3

import rx.*
import rx.lang.kotlin.*
import rx.schedulers.Schedulers
import java.net.URL
import java.net.UnknownHostException
import java.util.regex.Pattern

// See see http://blog.danlew.net/2014/09/22/grokking-rxjava-part-2/

fun getUrls(): Observable<List<String>> = Observable.just(listOf("http://www.heise.de", "http://www.golem.de", "http://www.arstechnica.com", "http://unkown_url", "http://wired.com"))


fun readTitle(urlStr: String): String? = grepTitle(readUrl(urlStr)) + " " +Thread.currentThread().getName()

internal fun grepTitle(urlText: String): String? =
    Regex("<title>(.*)</title>", RegexOption.DOT_MATCHES_ALL).find(urlText)?.groups?.get(1)?.value

internal fun readUrl(urlStr: String): String =
    URL(urlStr).readText() // Can throw errors for unknown urls

fun printUrls() {
    val urlListObservable: Observable<List<String>> = getUrls() // Observable with a list of urls
    val urlsObservable: Observable<String> = urlListObservable.flatMap { // Observable emitting individual urls
        Observable.from(it)
    }
    urlsObservable.subscribe {
        println("Url: $it")
    }
}

// Check http://tomstechnicalblog.blogspot.de/2015/11/rxjava-achieving-parallelization.html

fun <T> Observable<T>.debug(s: String): Observable<T> = this.doOnNext({println("onNext $s: ${it} - [${Thread.currentThread().getName()}]")})

fun printTitles() {
    val urlListObservable: Observable<List<String>> = getUrls() // Observable with a list of urls
    urlListObservable.flatMap { Observable.from(it) }// Observable emitting individual urls
            .debug("flatMap Url")
        .flatMap { Observable.just(it) }
            .debug("list")
        .subscribeOn(Schedulers.io())
            .debug("subscribeOn io")
        .map { readTitle(it) }
            .debug("map read title")
            .onErrorReturn { null }
            .debug("onError null")
        .filter { it != null }
            .debug("filter null")
        .toBlocking().forEach { println("Url: $it") }
}

fun showThreads() {
    Observable.just(1,2,3,4)
    .map { "$it: Map on thread: ${Thread.currentThread().getName()}"}
    .subscribeOn(Schedulers.computation())
    .observeOn(Schedulers.io())
    .subscribe() { println("$it subscribe on: ${Thread.currentThread().getName()}")}
}

fun main(args: Array<String>) {
    printUrls()
    printTitles()
    showThreads()
    Thread.sleep(20000)
}
