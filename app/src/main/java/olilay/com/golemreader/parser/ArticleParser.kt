package olilay.com.golemreader.parser

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import olilay.com.golemreader.models.Article
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ArticleParser(private val elem : Element,
                    private val parseManager : ParseManager) : AsyncTask<Void, Void, Article>() {

    private var doc : Document? = null // Article HTML

    override fun doInBackground(vararg void : Void) : Article {
        return parse()
    }

    override fun onPostExecute(result: Article?) {
        super.onPostExecute(result)

        parseManager.onArticleParsed(result!!)
    }

    private fun parse() : Article {
        val url = getUrl()

        return Article(
                getPreHeading(),
                getHeading(),
                url,
                getDescription(),
                getImage(),
                getDate(url.toString()),
                getAuthor(url.toString()),
                getAmountOfComments(),
                getContent(url.toString()))
    }

    private fun getPreHeading() : String {
        return elem.select("span[class=$GOLEM_ARTICLE_PRE_HEADING_CLASS]")?.first()?.text()
                ?: throw ParseException("Could not parse pre-heading for elem $elem")
    }

    private fun getHeading() : String {
        return elem.select("span[class=$GOLEM_ARTICLE_HEADING_CLASS]")?.first()?.text()
                ?: throw ParseException("Could not parse heading for elem $elem")
    }

    private fun getUrl() : URL {
        return URL(elem.select("a[class=$GOLEM_ARTICLE_LINK_CLASS][rel=$GOLEM_ARTICLE_LINK_REL]")
                .attr("href")
                ?: throw ParseException("Could not parse URL for elem $elem"))
    }

    private fun getImage() : Drawable {
        var imageString = elem.select("img[class=$GOLEM_ARTICLE_IMAGE_CLASS]")
                ?.attr("src")

        if (imageString == null || imageString == "") {
            imageString = elem.select("img[class=$GOLEM_ARTICLE_IMAGE_CLASS_ALT]")
                    ?.attr("data-src")
                    ?: return ParserUtils.urlToDrawable(null, parseManager.getOverviewActivity() as Activity)
        }

        return ParserUtils.urlToDrawable(imageString,  parseManager.getOverviewActivity() as Activity)
    }

    private fun getDescription() : String {
        return elem.select("p[class=$GOLEM_ARTICLE_DESCRIPTION_CLASS]")?.text()
                ?: throw ParseException("Could not parse description for elem $elem")
    }

    private fun getAmountOfComments() : Int {
        val parsedString = elem.select("a[class=$GOLEM_ARTICLE_COMMENT_COUNT_CLASS]")?.text()
                ?: throw ParseException("Could not get comment amount string for elem $elem")

        return parseCommentString(parsedString)
    }

    private fun getAuthor(url: String) : String {
        if (doc == null) {
            doc = getArticleDocument(url)
        }

        return doc!!.select("span[class=$GOLEM_ARTICLE_AUTHOR]")?.text()
                ?: throw ParseException("Could not get the author of ${elem.text()}")
    }

    private fun getDate(url: String) : Date {
        if (doc == null) {
            doc = getArticleDocument(url)
        }

        val dateString = doc!!.select("time[class=$GOLEM_ARTICLE_DATE]")?.text()
                ?: throw ParseException("Could not get the date of ${elem.text()}")

        return SimpleDateFormat("dd. MMMM yyyy, HH:mm", Locale.GERMANY).parse(dateString)
    }

    private fun getContent(url: String) : String {
        if (doc == null) {
            doc = getArticleDocument(url)
        }

        val commentLink = doc!!.select("p[class=link-comments]")?.html()
        val content = doc!!.select("article")
                ?: throw ParseException("Could not get content of ${elem.text()}")
        content.select("figure[class=hero]")?.remove()
        content.select("img")?.remove()
        content.select("div[class=authors authors--withsource]")?.remove()
        content.select("p")?.first()?.remove()
        content.select("ul[class=social-tools]")?.remove()
        content.select("div[class=tags]")?.remove()

        return content.html() + commentLink
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
     * Retrieves the article from a given Url.
     * @return The amount of comments.
     */
    private fun getArticleDocument(url : String) : Document {
        return ParserUtils.getDocument(url)
    }
}