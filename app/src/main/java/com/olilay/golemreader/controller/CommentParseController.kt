package com.olilay.golemreader.controller

import com.olilay.golemreader.activities.CommentActivity
import com.olilay.golemreader.models.comment.Comment
import com.olilay.golemreader.models.comment.CommentMetadata
import com.olilay.golemreader.parser.comment.CommentParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.lang.ref.WeakReference

class CommentParseController(
    commentActivity: CommentActivity,
    private val commentMetadata: CommentMetadata
) {
    private var commentActivity: WeakReference<CommentActivity> =
        WeakReference(commentActivity)
    private var commentParser = CommentParser()

    var parsing = false

    fun startParse() {
        if (!parsing) {
            parsing = true

            CoroutineScope(Dispatchers.Main).launch {
                val comments = commentParser.parseAsync(commentMetadata)
                onContentParsed(comments)
            }
        }
    }

    private fun onContentParsed(commentResult: Result<Comment>) {
        parsing = false

        if (commentResult.isSuccess) {
            commentActivity.get()!!.onRefreshFinished(commentResult.getOrThrow())
        } else {
            commentActivity.get()!!.onRefreshFailed(commentResult.exceptionOrNull() as Exception)
        }
    }
}