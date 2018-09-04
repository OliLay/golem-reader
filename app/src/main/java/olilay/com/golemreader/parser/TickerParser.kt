package olilay.com.golemreader.parser

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import olilay.com.golemreader.R
import olilay.com.golemreader.models.Article
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

const val GOLEM_URL = "https://www.golem.de"
const val GOLEM_ARTICLE_CLASS = "media__teaser media__teaser--articles hentry"
const val GOLEM_ARTICLE_HEADING_CLASS = "\"media__headline entry-title\""
const val GOLEM_ARTICLE_LINK_CLASS = "media__link"
const val GOLEM_ARTICLE_LINK_REL = "bookmark"
const val GOLEM_ARTICLE_IMAGE_CLASS = "photo"
const val GOLEM_ARTICLE_IMAGE_CLASS_ALT = "golem-data-afterload photo"
const val GOLEM_ARTICLE_DESCRIPTION_CLASS = "media__excerpt entry-content"
const val GOLEM_ARTICLE_COMMENT_COUNT_CLASS = "icon comment-count"
const val GOLEM_ARTICLE_DATE = "authors__pubdate"
const val GOLEM_ARTICLE_AUTHOR = "authors__name"

/**
 * Performs async network tasks to get and parse articles from Golem.de.
 * @author Oliver Layer
 */
class TickerParser(activity : Activity)  : AsyncTask<Void, Void, List<Article>>() {
    private var activity : WeakReference<Activity>? = null

    init {
        this.activity = WeakReference(activity)
    }

    override fun doInBackground(vararg void : Void) : List<Article> {
        return parse()
    }

    /**
     * Parses the Golem Ticker and extracts all articles.
     * @return Articles on the ticker populated with all meta-data.
     */
    private fun parse() : List<Article> {
        val doc = getDocument(GOLEM_URL)

        val articles = ArrayList<Article>()
        val elements = doc.getElementsByClass(GOLEM_ARTICLE_CLASS)

        elements.forEach{elem ->
            val heading = getHeading(elem)
            val url = getUrl(elem)
            val description = getDescription(elem)
            val image = getImage(elem)
            val authorAndDate = getAuthorAndDate(url.toString())
            val author = authorAndDate.first
            val date = authorAndDate.second
            val amountOfComments = getAmountOfComments(elem)

            articles.add(Article(heading, url, description, image, date, author, amountOfComments))
        }

        return articles
    }

    private fun getHeading(elem : Element) : String {
        return elem.select("h2[class=$GOLEM_ARTICLE_HEADING_CLASS]")?.first()?.text()
            ?: throw ParseException("Could not parse heading for elem $elem")
    }

    private fun getUrl(elem : Element) : URL {
        return URL(elem.select("a[class=$GOLEM_ARTICLE_LINK_CLASS][rel=$GOLEM_ARTICLE_LINK_REL]")
                .attr("href")
                ?: throw ParseException("Could not parse URL for elem $elem"))
    }

    private fun getImage(elem : Element) : Drawable {
        // used to return a default image when the online one can not be retrieved
        fun getDefaultDrawable() : Drawable {
            return ContextCompat.getDrawable(activity!!.get()!!.applicationContext, R.drawable.tooltip_frame_dark)!!
        }

        var imageString = elem.select("img[class=$GOLEM_ARTICLE_IMAGE_CLASS]")
                ?.attr("src")

        if (imageString == null || imageString == "") {
            imageString = elem.select("img[class=$GOLEM_ARTICLE_IMAGE_CLASS_ALT]")
                    ?.attr("data-src")
                    ?: return getDefaultDrawable()
        }

        return try {
            Drawable.createFromStream(URL(imageString).content as InputStream, null)
        }
        catch (e : Exception) {
            getDefaultDrawable()
        }
    }

    private fun getDescription(elem: Element) : String {
        return elem.select("p[class=$GOLEM_ARTICLE_DESCRIPTION_CLASS]")?.text()
                ?: throw ParseException("Could not parse description for elem $elem")
    }

    private fun getAmountOfComments(elem: Element) : Int {
        val parsedString = elem.select("a[class=$GOLEM_ARTICLE_COMMENT_COUNT_CLASS]")?.text()
                ?: throw ParseException("Could not get comment amount string for elem $elem")

        return try {
            parsedString.split(" ")[0].toInt()
        } catch (nfe: NumberFormatException) {
            throw ParseException("Could not cast $parsedString to Int.")
        } catch (be: ArrayIndexOutOfBoundsException) {
            throw ParseException("Could not split $parsedString at whitespace.")
        }
    }

    private fun getAuthorAndDate(url: String) : Pair<String, Date> {
        val doc = getDocument(url)
        val dateString = doc.select("time[class=$GOLEM_ARTICLE_DATE]")?.text()
                ?: throw ParseException("Could not get the date from $url")

        val date =
                SimpleDateFormat("dd. MMMM yyyy, HH:mm", Locale.GERMANY).parse(dateString)

        val author = doc.select("span[class=$GOLEM_ARTICLE_AUTHOR]")?.text()
                ?: throw ParseException("Could not get the author from $url")

        return Pair(author, date)
    }

    /**
     * Connects to given URL, then downloads and parses it using Jsoup.
     * @return The parsed document.
     */
    private fun getDocument(url: String) : Document {
        val con = Jsoup.connect(url)
        con.cookie("golem_view", "mobile") //ensure mobile view
        return con.get()
    }
}