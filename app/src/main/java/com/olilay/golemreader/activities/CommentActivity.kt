package com.olilay.golemreader.activities

import android.os.Bundle
import com.olilay.golemreader.R
import com.olilay.golemreader.adapter.PostAdapter
import com.olilay.golemreader.controller.CommentParseController
import com.olilay.golemreader.models.comment.Comment
import com.olilay.golemreader.models.comment.CommentMetadata

// TODO: move refresh etc. up, will remove lots of duplicate code
class CommentActivity : CardViewActivity(
    R.layout.activity_comment,
    R.id.comment_swiperefresh,
    R.id.comment_recycler_view
) {
    private lateinit var commentParseController: CommentParseController

    override fun onCreate(savedInstanceState: Bundle?) {
        val commentMetadata = intent.extras?.getParcelable<CommentMetadata>("commentMetadata")
            ?: throw RuntimeException("commentMetadata object not supplied by caller!")

        commentParseController = CommentParseController(this, commentMetadata)

        super.onCreate(savedInstanceState)
    }

    override fun refresh() {
        refreshLayout.isRefreshing = false // make SwipeRefreshLayout loading animation disappear

        if (!commentParseController.parsing) {
            setViewVisibility(true, R.id.comment_progress_bar)
            setViewVisibility(false, R.id.comment_recycler_view)
            setViewVisibility(false, R.id.comment_error_image)
            setViewVisibility(false, R.id.comment_error_message)

            commentParseController.startParse()
        }
    }

    fun onRefreshFinished(comment: Comment) {
        setViewVisibility(false, R.id.comment_progress_bar)
        setViewVisibility(true, R.id.comment_recycler_view)

        recyclerView.adapter = PostAdapter(comment.posts)
    }

    fun onRefreshFailed(e: Exception) {
        val message = when (e) {
            is java.io.IOException -> resources.getString(R.string.no_connection)
            else -> resources.getString(R.string.error)
        }

        showErrorMessage(
            message, R.id.comment_progress_bar, R.id.comment_error_image,
            R.id.comment_error_message
        )
    }
}
