package com.olilay.golemreader.activities

import android.os.Bundle
import android.webkit.WebView
import com.olilay.golemreader.R
import com.olilay.golemreader.models.MinimalArticle
import com.olilay.golemreader.parser.ArticleParseManager
import java.lang.Exception
import java.lang.RuntimeException

class ArticleActivity : AppActivity() {
    private lateinit var articleParseManager: ArticleParseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_article)
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        val minimalArticle = extras?.getParcelable("minimalArticle") as MinimalArticle?
                ?: throw RuntimeException("MinimalArticle object not supplied by caller!")

        articleParseManager = ArticleParseManager(minimalArticle, this)
        showArticle()
    }

    private fun showArticle() {
        if (!articleParseManager.parsing) {
            setViewVisibility(true, R.id.article_progress_bar)
            setViewVisibility(false, R.id.article_webview)
            setViewVisibility(false, R.id.article_error_image)
            setViewVisibility(false, R.id.article_error_message)

            articleParseManager.startParse()
        }
    }

    fun onContentParsed(content: String) {
        val styledContent = styleContent(content)
        setWebView(styledContent)
        setViewVisibility(true, R.id.article_webview)
    }

    /**
     * Gets called by [ArticleParseManager] when the retrieving of the article fails.
     */
    fun onParseFailed(e: Exception) {
        val message = when (e) {
            is java.net.UnknownHostException -> resources.getString(R.string.no_connection)
            else -> resources.getString(R.string.error)
        }

        showErrorMessage(message, R.id.article_progress_bar, R.id.article_error_image,
                R.id.article_error_message)
    }

    private fun setWebView(content: String) {
        val webView: WebView = findViewById(R.id.article_webview)
        webView.loadDataWithBaseURL(null, content, "text/html; charset=utf-8",
                "utf8", null)
    }

    private fun styleContent(content: String) : String {
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
              color: white;
            }
            </style>
            </head>
        """.trimIndent().format()
    }

    private fun getBackgroundColorHex() : String {
        return getColorHex(R.color.colorBackground)
    }

    private fun getTextColorHex() : String {
        return getColorHex(R.color.colorText)
    }

    private fun getColorHex(attribute: Int) : String {
        val color = getColor(attribute)
        return String.format("#%06X", (0xFFFFFF and color))

    }
}