package net.taobits.grokking_rxjava.part3

import rx.*
import rx.lang.kotlin.*
import rx.schedulers.Schedulers
import java.net.URL
import java.net.UnknownHostException
import java.util.regex.Pattern

// See see http://blog.danlew.net/2014/09/22/grokking-rxjava-part-2/

fun getUrls(): List<String> = listOf("http://www.heise.de", "http://www.golem.de", "http://www.arstechnica.com", "http://unkown_url", "http://wired.com")


fun readTitle(urlStr: String): String? = grepTitle(readUrl(urlStr)) + " " +Thread.currentThread().getName()

internal fun grepTitle(urlText: String): String? =
    Regex("<title>(.*)</title>", RegexOption.DOT_MATCHES_ALL).find(urlText)?.groups?.get(1)?.value

internal fun readUrl(urlStr: String): String {
    val startTime = System.currentTimeMillis()
    println("Start reading $urlStr")
    val text = URL(urlStr).readText() // Can throw errors for unknown urls
    println("Reading $urlStr took ${System.currentTimeMillis()-startTime} ms")
    return text
}

fun printUrls() {
    val urlListObservable: Observable<List<String>> = Observable.just(getUrls()) // Observable with a list of urls
    val urlsObservable: Observable<String> = urlListObservable.flatMap { // Observable emitting individual urls
        Observable.from(it)
    }
    //urlsObservable.subscribe { println("Url: $it") }
    urlsObservable.toBlocking().forEach { println("Url: $it") }
}

// Check http://tomstechnicalblog.blogspot.de/2015/11/rxjava-achieving-parallelization.html

fun <T> Observable<T>.debug(s: String): Observable<T> = this.doOnNext({println("onNext $s: ${it} - [${Thread.currentThread().getName()}]")})

fun printTitles() {
    val startTime = System.currentTimeMillis()
    val urlList: Observable<List<String>> = Observable.just(getUrls()) // Observable with a list of urls
    val urls: Observable<String> = urlList.flatMap { Observable.from(it) }// Observable emitting individual urls
                                    .debug("flatMap Url")
    val titles: Observable<String?> = urls.flatMap {
                    Observable.just(it)
                    .observeOn(Schedulers.computation())
                    .debug("observeOn computation")
                    .map {readTitle(it)}
                    .debug("map readTitle")
                    .onErrorReturn { null }
                    .debug("onError null")
            }
            .debug("flatMap parallel")
            .observeOn(Schedulers.io())
            .debug("observeOn io")
    val titlesWithoutErrors: Observable<String?> = titles.filter { it != null }
                                        .debug("filter null")
    titlesWithoutErrors.toBlocking().forEach { println("Url: $it") }
    println("Took ${System.currentTimeMillis()-startTime} ms")
}


fun main(args: Array<String>) {
    printUrls()
    printTitles()
}
