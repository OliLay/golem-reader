package com.olilay.golemreader.parser.comment

import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.util.Log
import com.olilay.golemreader.models.comment.Comment
import com.olilay.golemreader.models.comment.CommentMetadata
import com.olilay.golemreader.models.comment.Post
import com.olilay.golemreader.models.comment.page.FirstPage
import com.olilay.golemreader.models.comment.page.Page
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat
import java.util.*

class CommentParser {

    suspend fun parseAsync(commentMetadata: CommentMetadata): Result<Comment> {
        return withContext(Dispatchers.IO) {
            try {
                Result.success(parse(commentMetadata))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun parse(commentMetadata: CommentMetadata): Comment {
        val firstPage = FirstPage(commentMetadata.url)
        val pages = mutableListOf<Page>()
        pages.add(firstPage)
        pages.addAll(firstPage.furtherPages())

        val posts = mutableListOf<Post>()

        for (page in pages) {
            for (postElement in page.getPostElements()) {
                val author = getAuthor(postElement)

                if (author.isNotBlank()) {
                    val heading = getHeading(postElement)
                    val date = getDate(postElement)
                    val content = getContent(postElement)
                    posts.add(Post(author, heading, date, content))
                }
            }
        }

        return Comment(posts, commentMetadata)
    }

    private fun getAuthor(element: Element): String {
        val html = element.select("a").first()?.html() ?: ""
        return Html.fromHtml(html, FROM_HTML_MODE_COMPACT).toString()
    }

    private fun getHeading(element: Element): String {
        val html = element.select("h2").first()?.html() ?: ""
        return Html.fromHtml(html, FROM_HTML_MODE_COMPACT).toString()
    }

    private fun getDate(element: Element): Date {
        val dateString = element.select("h3")
            .first()
            ?.childNode(2)
            ?.outerHtml()
            ?.trim()

        return if (dateString == null) {
            Log.e("CommentParser", "Could not parse date, date string is null.")
            Date()
        } else {
            SimpleDateFormat("dd.MM.yy - HH:mm", Locale.GERMAN).parse(dateString) ?: Date()
        }
    }

    private fun getContent(element: Element): String {
        val html = element.select("p[class=msg-body]").first()?.html() ?: ""
        return Html.fromHtml(html, FROM_HTML_MODE_COMPACT).toString()
    }
}