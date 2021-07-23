package com.olilay.golemreader.models.article.page

import org.jsoup.select.Elements
import java.net.URL

class FirstPage(override var url: URL) : Page(url) {
    /**
     * Gets the link to the forum from a given [Elements] of an article.
     * @return Link in HTML. If it could not be parsed, empty [String].
     */
    fun getCommentLink(): URL {
        val extractedLink = getJsoupDocument().select("p[class=link-comments]")?.html()
        return URL(extractedLink)
    }
}