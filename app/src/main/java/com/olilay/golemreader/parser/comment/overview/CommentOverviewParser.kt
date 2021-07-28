package com.olilay.golemreader.parser.comment.overview

import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.util.Log
import com.olilay.golemreader.models.article.*
import com.olilay.golemreader.models.article.page.FirstPage
import com.olilay.golemreader.models.article.page.LaterPage
import com.olilay.golemreader.models.article.page.Page
import com.olilay.golemreader.models.comment.CommentMetadata
import com.olilay.golemreader.parser.helper.ParserUtils
import kotlinx.coroutines.*
import org.jsoup.nodes.Element
import java.lang.Exception
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
        val commentMetadatas = ArrayList<CommentMetadata>()

        val jsoupDocument = ParserUtils.getDocument(commentsUrl.toString())
        val commentElements = jsoupDocument.select("ol[class=list-comments]")
            .select("li")

        for (element in commentElements) {
            val heading = getHeading(element)

            if (heading.isNotBlank()) {
                val url = getUrl(element)
                val date = getDate(element)
                val author = getAuthor(element)
                val answerCount = getAnswerCount(element);

                val metadata = CommentMetadata(heading, url, author, date, answerCount)
                commentMetadatas.add(metadata)
            }
        }

        return commentMetadatas
    }

    private fun getHeading(element: Element): String {
        val html = element.select("a").first()?.html() ?: ""
        return Html.fromHtml(html, FROM_HTML_MODE_COMPACT).toString()
    }

    private fun getUrl(element: Element): URL {
        val urlString = element.select("a").attr("href")

        return try {
            URL(urlString)
        } catch (e: MalformedURLException) {
            Log.e("CommentOverviewParser", "Could not parse URL '$urlString'")
            throw e
        }
    }

    private fun getDate(element: Element): Date {
        val dateString = element.select("h3")
            .first()
            ?.childNode(2)
            ?.outerHtml()
            ?.trim()

        return if (dateString == null) {
            Log.e("CommentOverviewParser", "Could not parse date, date string is null.")
            Date()
        } else {
            SimpleDateFormat("dd.MM.yy HH:mm", Locale.GERMAN).parse(dateString) ?: Date()
        }
    }

    private fun getAuthor(element: Element): String {
        return element.select("strong").html()
    }

    private fun getAnswerCount(element: Element): Int {
        return element.select("p[class=count]").html().toIntOrNull() ?: 0
    }
}