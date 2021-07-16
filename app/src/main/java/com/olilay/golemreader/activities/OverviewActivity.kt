package com.olilay.golemreader.activities

import android.os.Bundle
import com.olilay.golemreader.R
import com.olilay.golemreader.adapter.ArticleAdapter
import com.olilay.golemreader.models.article.ArticleMetadata
import com.olilay.golemreader.parser.article.overview.TickerParseController
import java.lang.Exception

class OverviewActivity : CardViewActivity(
    R.layout.activity_overview,
    R.id.overview_swiperefresh,
    R.id.overview_recycler_view
) {
    private lateinit var tickerParseController: TickerParseController

    override fun onCreate(savedInstanceState: Bundle?) {
        tickerParseController = TickerParseController(this)

        super.onCreate(savedInstanceState)
    }

    override fun refresh() {
        refreshLayout.isRefreshing = false //make SwipeRefreshLayout loading animation disappear

        if (!tickerParseController.parsing) {
            setViewVisibility(true, R.id.overview_progress_bar)
            setViewVisibility(false, R.id.overview_recycler_view)
            setViewVisibility(false, R.id.overview_error_image)
            setViewVisibility(false, R.id.overview_error_message)

            tickerParseController.startParse()
        }
    }

    fun onRefreshFinished(articleMetadata: List<ArticleMetadata>) {
        setViewVisibility(false, R.id.overview_progress_bar)
        setViewVisibility(true, R.id.overview_recycler_view)

        recyclerView.adapter = ArticleAdapter(articleMetadata)
    }

    fun onRefreshFailed(e: Exception) {
        val message = when (e) {
            is java.net.UnknownHostException -> resources.getString(R.string.no_connection)
            else -> resources.getString(R.string.error)
        }

        showErrorMessage(
            message, R.id.overview_progress_bar, R.id.overview_error_image,
            R.id.overview_error_message
        )
    }
}
