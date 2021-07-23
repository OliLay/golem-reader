package com.olilay.golemreader.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import com.olilay.golemreader.R
import com.olilay.golemreader.models.article.ArticleMetadata
import com.olilay.golemreader.controller.ArticleParseController
import com.olilay.golemreader.models.article.Article
import java.lang.Exception
import java.lang.RuntimeException

class ArticleActivity : AppActivity() {
    private lateinit var articleParseController: ArticleParseController
    private lateinit var article: Article

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_article)
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        val articleMetadata = extras?.getParcelable("articleMetadata") as ArticleMetadata?
            ?: throw RuntimeException("articleMetadata object not supplied by caller!")

        articleParseController = ArticleParseController(articleMetadata, this)
        showArticle()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_article, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_comments) {
            switchToCommentActivity()
        }
        return true
    }

    private fun switchToCommentActivity() {
        val intent = Intent(this, CommentOverviewActivity::class.java)
        intent.putExtra("commentsUrl", )
        startActivity(intent)
    }

    private fun showArticle() {
        if (!articleParseController.parsing) {
            setViewVisibility(true, R.id.article_progress_bar)
            setViewVisibility(false, R.id.article_webview)
            setViewVisibility(false, R.id.article_error_image)
            setViewVisibility(false, R.id.article_error_message)

            articleParseController.startParse()
        }
    }

    fun onContentParsed(article: Article) {
        this.article = article

        val styledContent = styleContent(article.content)
        setWebViewContent(styledContent)
        setViewVisibility(true, R.id.article_webview)
    }

    /**
     * Gets called by [ArticleParseController] when the retrieving of the article fails.
     */
    fun onParseFailed(e: Exception) {
        val message = when (e) {
            is java.net.UnknownHostException -> resources.getString(R.string.no_connection)
            else -> resources.getString(R.string.error)
        }

        showErrorMessage(
            message, R.id.article_progress_bar, R.id.article_error_image,
            R.id.article_error_message
        )
    }

    private fun setWebViewContent(content: String) {
        val webView: WebView = findViewById(R.id.article_webview)
        webView.loadDataWithBaseURL(
            null, content, "text/html; charset=utf-8",
            "utf8", null
        )
    }

    private fun styleContent(content: String): String {
        return getStyleHtml() + content
    }

    private fun getStyleHtml(): String {
        return """
            <head>
            <style>
            body {
              background-color: ${getBackgroundColorHex()};
              color: ${getTextColorHex()};
            }
            a:link {
              color: ${getLinkColorHex()};
            }
            </style>
            </head>
        """.trimIndent().format()
    }

    private fun getBackgroundColorHex(): String {
        return getColorHex(R.color.colorBackground)
    }

    private fun getTextColorHex(): String {
        return getColorHex(R.color.colorText)
    }

    private fun getLinkColorHex() : String {
        return getColorHex(R.color.colorLink)
    }

    private fun getColorHex(attribute: Int): String {
        val color = getColor(attribute)
        return String.format("#%06X", (0xFFFFFF and color))
    }
}