package com.olilay.golemreader.parser.comment.overview

import android.util.Log
import com.olilay.golemreader.models.article.*
import com.olilay.golemreader.models.article.page.FirstPage
import com.olilay.golemreader.models.article.page.LaterPage
import com.olilay.golemreader.models.article.page.Page
import com.olilay.golemreader.models.comment.CommentMetadata
import com.olilay.golemreader.parser.helper.ParserUtils
import kotlinx.coroutines.*
import java.lang.Exception
import java.net.URL

class CommentOverviewParser {

    suspend fun parseAsync(commentsUrl: URL): Result<List<CommentMetadata>> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(parse(commentsUrl))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun parse(commentsUrl: URL): List<CommentMetadata> {
        val commentMetadatas : List<CommentMetadata> = ArrayList()

        var jsoupDocument = ParserUtils.getDocument(commentsUrl.toString())

        return ArrayList()
    }

    /**
     * Downloads the complete content of the given [URL] (Golem.de Article) and parses it.
     * @return A [String] that contains the content of the given article.
     */
    private fun parseArticle(url: URL): String {
        val firstPage = FirstPage(url)
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

        return overallContent + firstPage.getCommentLink()
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
                   // val pageUrl = URL(GOLEM_URL + hrefAttr)
                   // pages.add(LaterPage(pageUrl))
                } catch (e: Exception) {
                    Log.w("ArticleParser", "$elem is not a valid URL. Discarding!")
                }
            }
        }

        return pages
    }
}