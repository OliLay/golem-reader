package com.olilay.golemreader.models.article.page

import org.jsoup.select.Elements
import java.net.URL

class FirstPage(override var url: URL) : Page(url) {
    /**
     * Gets the URL to the forum from a given [Elements] of an article.
     */
    fun getCommentLink(): URL {
        val extractedLink = getJsoupDocument()
            .select("a[class=icon comment-count]")
            .first()
            .attr("href")
        return URL(extractedLink)
    }
}