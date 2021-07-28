package com.olilay.golemreader.parser.article

import android.util.Log
import com.olilay.golemreader.models.article.*
import com.olilay.golemreader.models.article.page.FirstPage
import com.olilay.golemreader.models.article.page.LaterPage
import com.olilay.golemreader.models.article.page.Page
import kotlinx.coroutines.*
import java.lang.Exception
import java.net.URL

const val GOLEM_URL = "https://golem.de"

/**
 * Handles downloading and parsing of an [Article].
 */
class ArticleParser {

    suspend fun parseAsync(articleMetadata: ArticleMetadata): Result<Article> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(parse(articleMetadata))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun parse(articleMetadata: ArticleMetadata): Article {
        val firstPage = FirstPage(articleMetadata.url)
        val pages: MutableSet<Page> = mutableSetOf(firstPage)
        var overallContent = ""

        if (hasArticleMultiplePages(firstPage)) {
            val furtherPages = getFurtherPages(firstPage)
            pages.addAll(furtherPages)
        }

        for (page in pages) {
            page.removeNotNeededContent()
            page.addStyling()
            overallContent += page.getArticleHtml()
        }

        val commentLink = firstPage.getCommentLink()
        return assembleArticle(articleMetadata, overallContent, commentLink)
    }

    private fun assembleArticle(articleMetadata: ArticleMetadata, content: String, commentsUrl: URL) : Article {
        return Article(
            articleMetadata.heading,
            articleMetadata.url,
            articleMetadata.description,
            articleMetadata.date,
            articleMetadata.amountOfComments,
            articleMetadata.imageUrl!!,
            content,
            commentsUrl
        )
    }

    /**
     * Checks if the article contains multiple pages
     * @return true if the article contains more than one page, else false
     */
    private fun hasArticleMultiplePages(firstPage: Page): Boolean {
        val listPagesIndicator = firstPage.getJsoupDocument().select("ol[class=list-pages]")
        return listPagesIndicator.size > 0
    }

    /**
     * Gets all [URL]s of the article's pages.
     * @return Set of URLs to the pages (no duplicates)
     */
    private fun getFurtherPages(firstPage: Page): MutableSet<Page> {
        val listPagesIndicator = firstPage.getJsoupDocument().select("ol[class=list-pages]")
        val aTags = listPagesIndicator.select("a")
        val pages: MutableSet<Page> = mutableSetOf()

        for (elem in aTags) {
            val hrefAttr = elem.attr("href")

            if (hrefAttr != null) {
                try {
                    val pageUrl = URL(GOLEM_URL + hrefAttr)
                    pages.add(LaterPage(pageUrl))
                } catch (e: Exception) {
                    Log.w("ArticleParser", "$elem is not a valid URL. Discarding!")
                }
            }
        }

        return pages
    }
}