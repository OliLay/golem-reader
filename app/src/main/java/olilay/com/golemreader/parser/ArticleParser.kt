package olilay.com.golemreader.parser

import android.os.AsyncTask
import android.util.Log
import olilay.com.golemreader.models.Article
import olilay.com.golemreader.models.MinimalArticle
import org.jsoup.nodes.Document
import java.lang.Exception

//TODO: feat: support 2+ page articles

/**
 * Handles downloading and parsing of an [Article].
 */
class ArticleParser(private val minimalArticle: MinimalArticle,
                    private val articleParseManager: ArticleParseManager) : AsyncTask<Void, Void, AsyncTaskResult<Article>>() {

    private var doc : Document? = null // Article HTML

    override fun doInBackground(vararg void : Void) : AsyncTaskResult<Article> {
        return try {
            AsyncTaskResult(parse(minimalArticle))
        } catch (e : Exception) {
            Log.e("RssParser", e.toString())
            AsyncTaskResult(minimalArticle as Article, e)
        }
    }

    override fun onPostExecute(result: AsyncTaskResult<Article>) {
        super.onPostExecute(result)

        articleParseManager.onContentParsed(result)
    }

    /**
     * Parses the given [MinimalArticle] into an [Article].
     *
     * @param minimalArticle The [MinimalArticle] to be parsed.
     * @return The [Article] with the [Article.content] field populated.
     */
    private fun parse(minimalArticle: MinimalArticle) : Article {
            return Article(
                    minimalArticle.heading,
                    minimalArticle.url,
                    minimalArticle.description,
                    minimalArticle.date,
                    minimalArticle.amountOfComments,
                    minimalArticle.imageUrl!!,
                    getContent(minimalArticle))
    }

    /**
     * Downloads the content of the given [MinimalArticle] and parses it.
     * @return A [String] that contains the content of the given article.
     */
    private fun getContent(minimalArticle: MinimalArticle) : String {
        if (doc == null) {
            doc = getArticleDocument(minimalArticle.url.toString())
        }

        val commentLink = doc!!.select("p[class=link-comments]")?.html()
        val content = doc!!.select("article")
                ?: throw ParseException("Could not get content of ${minimalArticle.heading}")
        content.select("figure[class=hero]")?.remove()
        content.select("img")?.remove()
        content.select("div[class=authors]")?.remove()
        content.select("div[class=authors authors--withsource]")?.remove()
        content.select("ul[class=golemGallery2]")?.remove()
        content.select("details[class=toc]")?.remove()
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