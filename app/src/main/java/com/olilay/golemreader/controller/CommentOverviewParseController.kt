package com.olilay.golemreader.controller

import com.olilay.golemreader.R
import com.olilay.golemreader.activities.CommentOverviewActivity
import com.olilay.golemreader.models.comment.CommentMetadata
import com.olilay.golemreader.parser.comment.overview.CommentOverviewParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.URL

class CommentOverviewParseController(
    commentOverviewActivity: CommentOverviewActivity,
    private val commentsUrl: URL
) {
    private var commentOverviewActivity: WeakReference<CommentOverviewActivity> =
        WeakReference(commentOverviewActivity)
    private var commentOverviewParser = CommentOverviewParser()

    var parsing = false

    fun startParse() {
        if (!parsing) {
            parsing = true

            CoroutineScope(Dispatchers.Main).launch {
                val comments = commentOverviewParser.parseAsync(commentsUrl)
                onContentParsed(comments)
            }
        }
    }

    private fun onContentParsed(commentOverviewResult: Result<List<CommentMetadata>>) {
        parsing = false

        if (commentOverviewResult.isSuccess) {
            commentOverviewActivity.get()!!.onRefreshFinished(commentOverviewResult.getOrThrow())
        } else {
            commentOverviewActivity.get()!!.onRefreshFailed(commentOverviewResult.exceptionOrNull() as Exception)
        }
    }
}