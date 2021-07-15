package com.olilay.golemreader.models.article.page

import java.net.URL

class LaterPage(override var url: URL) : Page(url) {

    override fun removeNotNeededContent() {
        super.removeNotNeededContent()
        removeHeading()
    }

    private fun removeHeading() {
        getArticleElements().select("h1[class=head5]")?.remove()
    }
}