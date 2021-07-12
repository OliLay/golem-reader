package com.olilay.golemreader.parser.helper

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object ParserUtils {
    /**
     * Connects to given URL, then downloads and parses it using Jsoup.
     * @return The parsed document.
     */
    fun getDocument(url: String): Document {
        val con = Jsoup.connect(url)
        con.cookie("golem_view", "mobile") // ensure mobile view
        con.cookie("golem_consent20", "cmp|210630") // accept tracking
        return con.get()
    }
}