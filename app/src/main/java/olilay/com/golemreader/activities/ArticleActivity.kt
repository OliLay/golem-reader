package olilay.com.golemreader.activities

import android.os.Bundle
import android.webkit.WebView
import olilay.com.golemreader.R
import olilay.com.golemreader.models.MinimalArticle
import olilay.com.golemreader.parser.ArticleParseManager
import java.lang.Exception
import java.lang.RuntimeException

class ArticleActivity : AppActivity() {
    private lateinit var articleParseManager : ArticleParseManager

    override fun onCreate(savedInstanceState : Bundle?) {
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
            setViewVisibility(false,  R.id.article_error_image)
            setViewVisibility(false,  R.id.article_error_message)

            articleParseManager.startParse()
        }
    }

    fun onContentParsed(content: String) {
        setWebView(content)
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

    private fun setWebView(content : String) {
        val webView : WebView = findViewById(R.id.article_webview)
        webView.loadData(content, "text/html", null)
    }
}