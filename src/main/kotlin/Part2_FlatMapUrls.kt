package net.taobits.grokking_rxjava.part2

import rx.*
import rx.lang.kotlin.*
import java.net.URL
import java.net.UnknownHostException
import java.util.regex.Pattern

// See see http://blog.danlew.net/2014/09/22/grokking-rxjava-part-2/

fun getUrls(): Observable<List<String>> = Observable.just(listOf("http://www.heise.de", "http://www.golem.de", "http://www.arstechnica.com", "http://unkown_url"))


fun readTitle(urlStr: String): String? {
    val urlText = readUrl(urlStr)
    return if (urlText == null) null else grepTitle(urlText)
}

internal fun grepTitle(urlText: String): String? =
    Regex("<title>(.*)</title>", RegexOption.DOT_MATCHES_ALL).find(urlText)?.groups?.get(1)?.value

internal fun readUrl(urlStr: String): String? =
    try {
        URL(urlStr).readText()
    } catch (e: UnknownHostException) { null } // Exception handled locally

fun printUrls() {
    val urlListObservable: Observable<List<String>> = getUrls() // Observable with a list of urls
    val urlsObservable: Observable<String> = urlListObservable.flatMap { // Observable emitting individual urls
        Observable.from(it)
    }
    urlsObservable.subscribe {
        println("Url: $it")
    }
}

fun printTitles() {
    val urlListObservable: Observable<List<String>> = getUrls() // Observable with a list of urls
    urlListObservable.flatMap { Observable.from(it) }// Observable emitting individual urls
        .flatMap { Observable.just(readTitle(it)) }
        .filter { it != null }
        .subscribe { println("Url: $it") }
}

fun main(args: Array<String>) {
    printUrls()
    printTitles()
}
