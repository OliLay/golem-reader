package com.olilay.golemreader.activities

import com.olilay.golemreader.R
import com.olilay.golemreader.adapter.ArticleAdapter
import com.olilay.golemreader.models.article.ArticleMetadata
import java.lang.Exception


class CommentOverviewActivity : CardViewActivity(
    R.layout.activity_comment_overview,
    R.id.comment_overview_swiperefresh,
    R.id.comment_overview_recycler_view
) {
    // TODO
    //private lateinit var tickerParseController: TickerParseController

    override fun refresh() {
        refreshLayout.isRefreshing = false // make SwipeRefreshLayout loading animation disappear
    // TODO
      //  if (!tickerParseController.parsing) {
//            setViewVisibility(true, R.id.overview_progress_bar)
  //          setViewVisibility(false, R.id.overview_recycler_view)
    //        setViewVisibility(false, R.id.overview_error_image)
      //      setViewVisibility(false, R.id.overview_error_message)

         //   tickerParseController.startParse()
        //}
    }

    fun onRefreshFinished(articleMetadata: List<ArticleMetadata>) {
        setViewVisibility(false, R.id.overview_progress_bar)
        setViewVisibility(true, R.id.overview_recycler_view)

        recyclerView.adapter = ArticleAdapter(articleMetadata)
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
