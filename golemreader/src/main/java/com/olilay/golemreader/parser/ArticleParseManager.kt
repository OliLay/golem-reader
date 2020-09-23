package com.olilay.golemreader.parser

import com.olilay.golemreader.activities.ArticleActivity
import com.olilay.golemreader.models.Article
import com.olilay.golemreader.models.MinimalArticle
import java.lang.ref.WeakReference

/**
 * Used for managing processes that belong to one particular article, e.g. getting details
 * of an article. (for now content using [ArticleParser], later on also comments)
 *
 * @author Oliver Layer
 */
class ArticleParseManager(private val minimalArticle: MinimalArticle,
                          articleActivity: ArticleActivity) {
    private var articleActivity : WeakReference<ArticleActivity> = WeakReference(articleActivity)
    var parsing = false

    fun startParse() {
        if (!parsing) {
            parsing = true

            ArticleParser(minimalArticle, this).execute()

        }
    }

    /**
       Gets called when [ArticleParser] finished parsing the content of an [MinimalArticle].
     */
    fun onContentParsed(articleTaskResult : AsyncTaskResult<Article>) {
        parsing = false

        val error = articleTaskResult.error

        if (error != null) {
            articleActivity.get()!!.onParseFailed(error)
        } else {
            articleActivity.get()!!.onContentParsed(articleTaskResult.taskResult.content)
        }
    }
}