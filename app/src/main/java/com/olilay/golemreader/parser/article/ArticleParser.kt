package com.olilay.golemreader.parser.article

import android.util.Log
import com.olilay.golemreader.models.article.Article
import com.olilay.golemreader.models.article.ArticleMetadata
import com.olilay.golemreader.models.article.Page
import com.olilay.golemreader.parser.exception.ParseException
import com.olilay.golemreader.parser.helper.ParserUtils
import kotlinx.coroutines.*
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
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

    /**
     * Parses the given [ArticleMetadata] into an [Article].
     *
     * @param articleMetadata The [ArticleMetadata] to be parsed.
     * @return The [Article] with the [Article.content] field populated.
     */
    private fun parse(articleMetadata: ArticleMetadata): Article {
        return Article(
            articleMetadata.heading,
            articleMetadata.url,
            articleMetadata.description,
            articleMetadata.date,
            articleMetadata.amountOfComments,
            articleMetadata.imageUrl!!,
            getJsoupDocument(articleMetadata.url)
        )
    }

    /**
     * Downloads the complete content of the given [URL] (Golem.de Article) and parses it.
     * @return A [String] that contains the content of the given article.
     */
    private fun getJsoupDocument(url: URL): String {
        val firstPage = Page(url, true, getJsoupDocument(url.toString()))
        var overallContent: String

        if (hasMultiplePages(firstPage)) {
            val pages: MutableSet<URL> = getFurtherPagesUrls(firstPage)

            // TODO: continue
            // we can now remove the page selector from the first page as we got all our data
            firstPageContent = removePageSelector(firstPageContent)
            overallContent = firstPageContent.html()

            for (page in pageUrlSet) {
                overallContent += getLaterPage(page)
            }
        } else {
            overallContent = firstPageContent.html()
        }

        return overallContent + getCommentLink(firstPageDocument.allElements)
    }

    /**
     * Gets a page and parses it.
     * @param url The [URL] of the page.
     * @return HTML data of the given page.
     */
    private fun getLaterPage(url: URL): String {
        val doc = getJsoupDocument(url.toString())

        var content = removeNotNeededContent(doc.allElements)
        content = removeLaterPagesHeading(content)
        content = removePageSelector(content)
        return content.html()
    }

    /**
     * Gets the link to the forum from a given [Elements] of an article.
     * @return Link in HTML. If it could not be parsed, empty [String].
     */
    private fun getCommentLink(elements: Elements): String {
        return elements.select("p[class=link-comments]")?.html() ?: ""
    }

    /**
     * Adds an separator to the HTML, so that the ads are separated from the article content.
     * @param elements All [Elements] of the article.
     * @return [Elements] of the article.
     */
    private fun insertAdSeparators(elements: Elements): Elements {
        val separatorHtml = "<hr>"
        elements.select("section[class=supplementary]").before(separatorHtml).after(separatorHtml)
        elements.select("div[class=gbox_affiliate]").before(separatorHtml).after(separatorHtml)

        val gbox = elements.select("a[class=gbox_btn]")
        val attr = gbox.attr("data-cta")
        elements.select("a[class=gbox_btn]").html(attr)

        return elements
    }

    /**
     * Alters the content of the article page. This mostly is related to removing content that
     * is not supported (such as videos) or styling the article.
     * @param elements All [Elements] of the article.
     * @return [Elements] of the article.
     */
    private fun alterContent(elements: Elements): Elements {
        return insertAdSeparators(removeNotNeededContent(elements))
    }

    /**
     * Removes unnecessary content from the page (videos, images, ...).
     * @return [Elements] of the cleaned page
     */
    private fun removeNotNeededContent(elements: Elements): Elements {
        val content = elements.select("article")
            ?: throw ParseException("Could not get content of $elements")
        content.select("figure[class=hero]")?.remove()
        content.select("img")?.remove()
        content.select("div[class=authors]")?.remove()
        content.select("div[class=authors authors--withsource]")?.remove()
        content.select("ul[class=golemGallery2]")?.remove()
        content.select("details[class=toc]")?.remove()
        content.select("p")?.first()?.remove()
        content.select("ul[class=social-tools]")?.remove()
        content.select("div[class=tags]")?.remove()
        content.select("div[class=topictags]")?.remove()

        return content
    }

    /**
     * Removes the heading of later pages of the article.
     * @return [Document] of the page without the heading.
     */
    private fun removeLaterPagesHeading(elements: Elements): Elements {
        elements.select("h1[class=head5]")?.remove()
        return elements
    }

    /**
     * Checks if the article contains multiple pages
     * @return true if the article contains more than one page, else false
     */
    private fun hasMultiplePages(firstPage: Page): Boolean {
        val listPagesIndicator = firstPage.jsoupDocuments.select("ol[class=list-pages]")

        return listPagesIndicator.size > 0
    }

    /**
     * Gets all [URL]s of the article's pages.
     * @return Set of URLs to the pages (no duplicates)
     */
    private fun getFurtherPagesUrls(firstPage: Page): Set<Page> {
        val listPagesIndicator = firstPage.jsoupDocuments.select("ol[class=list-pages]")
        val aTags = listPagesIndicator.select("a")
        val pages: Set<Page> = mutableSetOf()

        for (elem in aTags) {
            val hrefAttr = elem.attr("href")

            if (hrefAttr != null) {
                try {
                    val pageUrl = URL(GOLEM_URL + hrefAttr)
                    pages.add(Page(pageUrl, false)))
                } catch (e: Exception) {
                    Log.w("ArticleParser", "$elem is not a valid URL. Discarding!")
                }
            }
        }

        return pages
    }

    /**
     * Removes the page selector from the page.
     * @return [Elements] without the page selector.
     */
    private fun removePageSelector(elements: Elements): Elements {
        elements.select("ol[class=list-pages]")?.remove()
        return elements
    }
}