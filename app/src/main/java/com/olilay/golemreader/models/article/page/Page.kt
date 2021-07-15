package com.olilay.golemreader.models.article.page

import com.olilay.golemreader.parser.helper.ParserUtils
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.URL

abstract class Page(
    open var url: URL
) {
    private var jsoupDocument: Document? = null
    private var elements: Elements? = null

    open fun addStyling() {
        insertAdSeparators()
        restoreAdVisibility()
    }

    open fun removeNotNeededContent() {
        removeUnsupportedElements()
        removePageSelector()
    }

    private fun removeUnsupportedElements() {
        val articleElements = getArticleElements();
        articleElements.select("figure[class=hero]")?.remove()
        articleElements.select("img")?.remove()
        articleElements.select("div[class=authors]")?.remove()
        articleElements.select("div[class=authors authors--withsource]")?.remove()
        articleElements.select("ul[class=golemGallery2]")?.remove()
        articleElements.select("details[class=toc]")?.remove()
        articleElements.select("p")?.first()?.remove()
        articleElements.select("ul[class=social-tools]")?.remove()
        articleElements.select("div[class=tags]")?.remove()
        articleElements.select("div[class=topictags]")?.remove()
    }


    private fun removePageSelector() {
        getArticleElements().select("ol[class=list-pages]")?.remove()
    }

    /**
     * Adds an separator to the HTML, so that the ads are separated from the article content.
     */
    private fun insertAdSeparators() {
        val separatorHtml = "<hr>"
        getArticleElements().select("section[class=supplementary]").before(separatorHtml)
            .after(separatorHtml)
        getArticleElements().select("div[class=gbox_affiliate]").before(separatorHtml).after(separatorHtml)
    }

    private fun restoreAdVisibility() {
        val gBox = getArticleElements().select("a[class=gbox_btn]")
        val attr = gBox.attr("data-cta")
        getArticleElements().select("a[class=gbox_btn]").html(attr)
    }

    fun getHtml(): String {
        return getArticleElements().html()
    }

    fun getJsoupDocument(): Document {
        if (jsoupDocument == null) {
            jsoupDocument = ParserUtils.getDocument(url.toString())
        }
        return jsoupDocument as Document
    }

    fun getArticleElements(): Elements {
        if (elements == null) {
            elements = getJsoupDocument().allElements.select("article")
        }

        return elements!!
    }

}