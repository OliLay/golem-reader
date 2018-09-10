package olilay.com.golemreader.activities

import android.os.Bundle
import android.webkit.WebView
import olilay.com.golemreader.R

class ArticleActivity : AppActivity() {
    lateinit var content : String

    override fun onCreate(savedInstanceState : Bundle?) {
        setContentView(R.layout.activity_article)
        super.onCreate(savedInstanceState)

        val extras = intent.extras
        content = extras?.getString("content")
            ?: throw RuntimeException("Article Content not supplied!")

        setWebview()
    }

    private fun setWebview() {
        val webView : WebView = findViewById<WebView>(R.id.article_webview)
        webView.loadData(content, "text/html", null)
    }
}