package com.olilay.golemreader.parser.comment.overview

import com.olilay.golemreader.activities.ArticleActivity
import com.olilay.golemreader.activities.CommentOverviewActivity
import com.olilay.golemreader.models.article.Article
import com.olilay.golemreader.models.article.ArticleMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.ref.WeakReference

class CommentOverviewParseController(
    private val article: Article,
    commentOverviewActivity: CommentOverviewActivity
) {
    private var commentOverviewActivity: WeakReference<CommentOverviewActivity> =
        WeakReference(commentOverviewActivity)
    var parsing = false

    fun startParse() {
        if (!parsing) {
            parsing = true

            CoroutineScope(Dispatchers.Main).launch {
                // TODO
            }
        }
    }

    private fun onContentParsed(articleTaskResult: Result<Article>) {
        parsing = false

        if (articleTaskResult.isSuccess) {
            //       commentOverviewActivity.get()!!.onRefreshFinished(articleTaskResult.getOrThrow().content)
        } else {
            //      commentOverviewActivity.get()!!.onRefreshFailed(articleTaskResult.exceptionOrNull() as Exception)
        }
    }
}