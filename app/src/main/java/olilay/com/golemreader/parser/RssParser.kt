package olilay.com.golemreader.parser

import android.os.AsyncTask
import olilay.com.golemreader.models.MinimalArticle
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import org.xmlpull.v1.XmlPullParserFactory

const val GOLEM_RSS_URL = "https://rss.golem.de/rss.php?feed=RSS2.0"

class RssParser (private val parseManager: ParseManager) : AsyncTask<Void, Void, List<MinimalArticle>>() {
    override fun doInBackground(vararg void : Void) : List<MinimalArticle> {
        return parse()
    }

    override fun onPostExecute(result: List<MinimalArticle>) {
        super.onPostExecute(result)

        parseManager.onTickerParsed(result)
    }

    private fun parse() : List<MinimalArticle> {
        val data = getData()
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        val articles = ArrayList<MinimalArticle>()

        parser.setInput(StringReader(data))

        var event = parser.eventType
        var isItem = false

        var title = ""
        var description = ""
        lateinit var url : URL
        lateinit var date : Date
        lateinit var imageUrl : URL
        var amountCommentsString = -1

        while (event != XmlPullParser.END_DOCUMENT) {
            val name = parser.name

            when (event) {
                XmlPullParser.START_TAG ->
                    when (name) {
                        "item" -> isItem = true
                        else -> {
                            if (isItem) {
                                when (name) {
                                    "title" -> title = readText(parser)
                                    "link" -> url = readUrl(parser)
                                    "description" -> description = readDescription(parser)
                                    "pubDate" -> date = readDate(parser)
                                    "encoded" -> imageUrl = readImageUrl(parser)
                                    "comments" -> {
                                        //check if we got the right tag
                                        if (parser.namespace == "http://purl.org/rss/1.0/modules/slash/") {
                                            amountCommentsString = readAmountOfComments(parser)
                                        }
                                    }
                                }
                            }
                        }
                    }
                XmlPullParser.END_TAG ->
                    when (name) {
                        "item" -> {
                            articles.add(MinimalArticle(title, url, description, date, imageUrl, amountCommentsString))
                            isItem = false
                        }
                    }
            }
            event = parser.next()
        }

        return articles
    }

    private fun getData() : String {
        val doc = ParserUtils.getDocument(GOLEM_RSS_URL)
        return doc.toString()
    }

    private fun readDescription(parser: XmlPullParser) : String {
        return "(\\(<a href)+(.)*( />)+".toRegex().replace(readText(parser), "")
    }

    private fun readImageUrl(parser: XmlPullParser) : URL {
        val urlString = "(https://)(.)*(jpg)".toRegex().find(readText(parser))!!.value

        return URL(urlString)
    }

    private fun readUrl(parser: XmlPullParser) : URL {
        return URL(readText(parser))
    }

    private fun readAmountOfComments(parser: XmlPullParser) : Int {
        val amountOfComments = readText(parser)

        return if (amountOfComments == "") {
            0
        } else {
            amountOfComments.toInt()
        }
    }

    private fun readDate(parser: XmlPullParser) : Date {
        //Sun, 23 Sep 2018 18:26:13 +0200
        return SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US)
                .parse(readText(parser))
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            //remove line breaks
            result = result.replace("\n", "").trim()
            parser.nextTag()
        }
        return result
    }
}