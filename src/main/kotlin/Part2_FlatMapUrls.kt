import rx.*
import rx.lang.kotlin.*
import java.net.URL
import java.util.regex.Pattern

// See see http://blog.danlew.net/2014/09/22/grokking-rxjava-part-2/

fun query(): Observable<List<String>> = Observable.just(listOf("http://www.heise.de", "http://www.golem.de", "http://www.arstechnica.com"))

fun readTitle(urlStr: String): String? {
    val url = URL(urlStr)
    val urlText = url.readText()
    return Regex("<title>(.*)</title>", RegexOption.DOT_MATCHES_ALL).find(urlText)?.groups?.get(1)?.value
}

fun main(args: Array<String>) {
    val urlListObservable: Observable<List<String>> = query() // Observable with a list of urls
    val urlsObservable: Observable<String> = urlListObservable.flatMap { // Observable emitting individual urls
        Observable.from(it)
    }
    val titleObservalbe: Observable<String> = urlsObservable.flatMap {
        observable() { readTitle }
    }
    urlsObservable.subscribe {
        println("Url: $it")
    }
    println(readTitle("http://www.heise.de"))
}
