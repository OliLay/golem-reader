package olilay.com.golemreader.parser

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import olilay.com.golemreader.models.Article
import olilay.com.golemreader.models.MinimalArticle
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ArticleParser(private val minimalArticle: MinimalArticle,
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
        return Article(
                minimalArticle.heading,
                minimalArticle.url,
                minimalArticle.description,
                getImage(),
                minimalArticle.date,
                minimalArticle.amountOfComments,
                getContent())
    }

    private fun getImage() : Drawable {
        return ParserUtils.urlToDrawable(minimalArticle.imageUrl.toString(),
                parseManager.getOverviewActivity() as Activity)
    }

    private fun getContent() : String {
        if (doc == null) {
            doc = getArticleDocument(minimalArticle.url.toString())
        }

        val commentLink = doc!!.select("p[class=link-comments]")?.html()
        val content = doc!!.select("article")
                ?: throw ParseException("Could not get content of ${minimalArticle.heading}")
        content.select("figure[class=hero]")?.remove()
        content.select("img")?.remove()
        content.select("div[class=authors authors--withsource]")?.remove()
        content.select("p")?.first()?.remove()
        content.select("ul[class=social-tools]")?.remove()
        content.select("div[class=tags]")?.remove()

        return content.html() + commentLink
    }

    /**
     * Retrieves the article from a given Url.
     * @return The amount of comments.
     */
    private fun getArticleDocument(url : String) : Document {
        return ParserUtils.getDocument(url)
    }
}