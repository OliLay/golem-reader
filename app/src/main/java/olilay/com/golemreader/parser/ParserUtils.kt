package olilay.com.golemreader.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

object ParserUtils {
    /**
     * Connects to given URL, then downloads and parses it using Jsoup.
     * @return The parsed document.
     */
    fun getDocument(url: String) : Document {
        val con = Jsoup.connect(url)
        con.cookie("golem_view", "mobile") //ensure mobile view
        return con.get()
    }
}