package net.taobits.grokking_rxjava.part3

import io.kotlintest.specs.FunSpec

class Part2_flatMapUrls_test : FunSpec() { init {
    test("grepTitle and no title found") {
        grepTitle("Not title") shouldBe null
        grepTitle("<title>Only half title") shouldBe null
    }
    test("grepTitle") {
        val textTab = table(
                headers("text",                              "title" ),
                row("Empty title <title></title>",           ""),
                row("<title>Simple title</title>",           "Simple title"),
                row("<title>Title\n with linebreak</title>", "Title\n with linebreak")
            )
        forAll(textTab) { text, title ->
            grepTitle(text) shouldBe title
        }
    }
}}