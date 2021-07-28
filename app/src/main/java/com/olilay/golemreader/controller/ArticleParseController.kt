package com.olilay.golemreader.controller

import com.olilay.golemreader.activities.ArticleActivity
import com.olilay.golemreader.models.article.Article
import com.olilay.golemreader.models.article.ArticleMetadata
import com.olilay.golemreader.parser.article.ArticleParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.ref.WeakReference

/**
 * Used for managing processes that belong to one particular article, e.g. getting details
 * of an article. (for now content using [ArticleParser], later on also comments)
 */
class ArticleParseController(
    private val articleMetadata: ArticleMetadata,
    articleActivity: ArticleActivity
) {
    private var articleActivity: WeakReference<ArticleActivity> = WeakReference(articleActivity)
    private var articleParser: ArticleParser = ArticleParser()
    var parsing = false

    fun startParse() {
        if (!parsing) {
            parsing = true

            CoroutineScope(Dispatchers.Main).launch {
                val article = articleParser.parseAsync(articleMetadata)
                onContentParsed(article)
            }
        }
    }

    /**
    Gets called when [ArticleParser] finished parsing the content of an [ArticleMetadata].
     */
    private fun onContentParsed(articleTaskResult: Result<Article>) {
        parsing = false

        if (articleTaskResult.isSuccess) {
            articleActivity.get()!!.onContentParsed(articleTaskResult.getOrThrow())
        } else {
            articleActivity.get()!!.onParseFailed(articleTaskResult.exceptionOrNull() as Exception)
        }
    }
}