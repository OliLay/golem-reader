package com.olilay.golemreader.activities

import android.os.Bundle
import com.olilay.golemreader.R
import com.olilay.golemreader.adapter.ArticleAdapter
import com.olilay.golemreader.models.article.ArticleMetadata
import com.olilay.golemreader.controller.CommentOverviewParseController
import com.olilay.golemreader.models.comment.CommentMetadata
import java.lang.Exception


class CommentOverviewActivity : CardViewActivity(
    R.layout.activity_comment_overview,
    R.id.comment_overview_swiperefresh,
    R.id.comment_overview_recycler_view
) {
    private var commentOverviewParseController = CommentOverviewParseController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun refresh() {
        refreshLayout.isRefreshing = false // make SwipeRefreshLayout loading animation disappear

        if (!commentOverviewParseController.parsing) {
            setViewVisibility(true, R.id.comment_overview_progress_bar)
            setViewVisibility(false, R.id.comment_overview_recycler_view)
            setViewVisibility(false, R.id.comment_overview_error_image)
            setViewVisibility(false, R.id.comment_overview_error_message)

            commentOverviewParseController.startParse()
        }
    }

    fun onRefreshFinished(articleMetadata: List<CommentMetadata>) {
        setViewVisibility(false, R.id.overview_progress_bar)
        setViewVisibility(true, R.id.overview_recycler_view)

        // TODO:
        //recyclerView.adapter = ArticleAdapter(articleMetadata)
    }

    fun onRefreshFailed(e: Exception) {
        val message = when (e) {
            is java.io.IOException -> resources.getString(R.string.no_connection)
            else -> resources.getString(R.string.error)
        }

        showErrorMessage(
            message, R.id.overview_progress_bar, R.id.overview_error_image,
            R.id.overview_error_message
        )
    }
}
