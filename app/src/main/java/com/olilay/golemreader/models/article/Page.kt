package com.olilay.golemreader.models.article

import com.olilay.golemreader.parser.helper.ParserUtils
import org.jsoup.nodes.Document
import java.net.URL

data class Page(
    var url: URL,
    var isFirstPage: Boolean,
    private var jsoupDocument: Document?,
) {
    fun getHtml(): String {
        return getJsoupDocument().html()
    }

    fun getJsoupDocument(): Document {
        if (jsoupDocument == null) {
            jsoupDocument = ParserUtils.getDocument(url.toString())
        }
        return jsoupDocument as Document
    }
}