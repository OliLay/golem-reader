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
const val GOLEM_MAIN_CLASS = "leader"
const val GOLEM_ARTICLE_PRE_HEADING_CLASS = "media__kicker"
const val GOLEM_ARTICLE_HEADING_CLASS = "media__headline-text"
const val GOLEM_MAIN_PRE_HEADING_CLASS = "head-meta"
const val GOLEM_MAIN_HEADING_CLASS = "head-main"
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

        articles.add(getMainArticle(doc))
        elements.forEach{elem ->
            val preHeading = getPreHeading(elem)
            val heading = getHeading(elem)
            val url = getUrl(elem)
            val description = getDescription(elem)
            val image = getImage(elem)
            val authorAndDate = getAuthorAndDate(url.toString())
            val author = authorAndDate.first
            val date = authorAndDate.second
            val amountOfComments = getAmountOfComments(elem)

            articles.add(Article(preHeading, heading, url, description, image, date, author, amountOfComments))
        }

        return articles
    }

    private fun getMainArticle(doc: Document) : Article {
        val elements = doc.getElementsByClass(GOLEM_MAIN_CLASS)

        val preHeading = elements.select("h2[class=$GOLEM_MAIN_PRE_HEADING_CLASS]")?.first()?.text()
                ?: throw ParseException("Could not parse pre-heading for main article")
        val heading = elements.select("h1[class=$GOLEM_MAIN_HEADING_CLASS]")?.first()?.text()
                ?: throw ParseException("Could not parse heading for main article")
        val url = elements.select("a")?.first()?.attr("href")
                ?: throw ParseException("Could not parse url for main article")
        val description = elements.select("p")?.first()?.text()
                ?: throw ParseException("Could not parse description for main article")
        val image = urlToDrawable(elements.select("img")?.attr("src")
                ?: throw ParseException("Could not parse image url for main article"))
        val authorAndDate = getAuthorAndDate(url)
        val author = authorAndDate.first
        val date = authorAndDate.second
        val amountOfComments = parseCommentString(elements.select("p")[1]?.text()
                ?: throw ParseException("Could not parse amount of comments for main article"))


        return Article(preHeading, heading, URL(url), description, image, date, author, amountOfComments)
    }

    private fun getPreHeading(elem : Element) : String {
        return elem.select("span[class=$GOLEM_ARTICLE_PRE_HEADING_CLASS]")?.first()?.text()
                ?: throw ParseException("Could not parse pre-heading for elem $elem")
    }

    private fun getHeading(elem : Element) : String {
        return elem.select("span[class=$GOLEM_ARTICLE_HEADING_CLASS]")?.first()?.text()
                ?: throw ParseException("Could not parse heading for elem $elem")
    }

    private fun getUrl(elem : Element) : URL {
        return URL(elem.select("a[class=$GOLEM_ARTICLE_LINK_CLASS][rel=$GOLEM_ARTICLE_LINK_REL]")
                .attr("href")
                ?: throw ParseException("Could not parse URL for elem $elem"))
    }

    private fun getImage(elem : Element) : Drawable {
        var imageString = elem.select("img[class=$GOLEM_ARTICLE_IMAGE_CLASS]")
                ?.attr("src")

        if (imageString == null || imageString == "") {
            imageString = elem.select("img[class=$GOLEM_ARTICLE_IMAGE_CLASS_ALT]")
                    ?.attr("data-src")
                    ?: return urlToDrawable(null)
        }

        return urlToDrawable(imageString)
    }

    private fun getDescription(elem: Element) : String {
        return elem.select("p[class=$GOLEM_ARTICLE_DESCRIPTION_CLASS]")?.text()
                ?: throw ParseException("Could not parse description for elem $elem")
    }

    private fun getAmountOfComments(elem: Element) : Int {
        val parsedString = elem.select("a[class=$GOLEM_ARTICLE_COMMENT_COUNT_CLASS]")?.text()
                ?: throw ParseException("Could not get comment amount string for elem $elem")

        return parseCommentString(parsedString)
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

    /**
     * Parses comment string (e.g. "42 Kommentare") to get the amount of comments (e.g. 42).
     * @return The amount of comments.
     */
    private fun parseCommentString(string: String) : Int {
        return try {
            string.split(" ")[0].toInt()
        } catch (nfe: NumberFormatException) {
            throw ParseException("Could not cast $string to Int.")
        } catch (be: ArrayIndexOutOfBoundsException) {
            throw ParseException("Could not split $string at whitespace.")
        }
    }

    /**
     * Expects an URL in form of a String an tries to get the image in form of a Drawable. If
     * it can not resolve the image behind the URL, it returns a default Drawable.
     * @return The URL where the image is.
     */
    private fun urlToDrawable(urlString: String?) : Drawable {
        fun getDefaultDrawable() : Drawable {
            //TODO: use proper default image
            return ContextCompat.getDrawable(activity!!.get()!!.applicationContext, R.drawable.tooltip_frame_dark)!!
        }

        if (urlString == null || urlString == "") {
            return getDefaultDrawable()
        }

        return try {
            Drawable.createFromStream(URL(urlString).content as InputStream, null)
        } catch (e : Exception) {
            getDefaultDrawable()
        }
    }
}