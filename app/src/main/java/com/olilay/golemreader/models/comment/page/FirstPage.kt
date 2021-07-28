package com.olilay.golemreader.models.comment.page

import java.net.URL

class FirstPage(override var url: URL) : Page(url) {

    private fun hasFurtherPages(): Boolean {
        /**
         * one ol[class=list-pages] is always the button to switch topics,
         * second one is for switching comment pages
         */
        return getJsoupDocument().select("ol[class=list-pages]").size >= 2
    }

    fun furtherPages(): List<Page> {
        if (!hasFurtherPages()) {
            return mutableListOf()
        }

        val pageRedirectElements = getJsoupDocument()
            .select("ol[class=list-pages]")[1]
            .select("li")
            .select("a")

        val pages = mutableListOf<Page>()
        for (redirectElement in pageRedirectElements) {
            val urlString = redirectElement.attr("href")
            val page = Page(URL(urlString))
            pages.add(page)
        }

        return pages
    }
}