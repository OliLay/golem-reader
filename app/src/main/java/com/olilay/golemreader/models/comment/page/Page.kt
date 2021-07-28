package com.olilay.golemreader.models.comment.page

import com.olilay.golemreader.parser.helper.ParserUtils
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.URL

open class Page(
    open var url: URL
) {
    private var jsoupDocument: Document? = null
    private var elements: Elements? = null

    fun getJsoupDocument(): Document {
        if (jsoupDocument == null) {
            jsoupDocument = ParserUtils.getDocument(url.toString())
        }
        return jsoupDocument as Document
    }

    fun getPostElements(): Elements {
        if (elements == null) {
            elements = getJsoupDocument()
                .select("ol[class=list-comments]")
                .select("li")
        }

        return elements!!
    }

}