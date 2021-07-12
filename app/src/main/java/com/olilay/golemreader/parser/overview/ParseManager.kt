package com.olilay.golemreader.parser.overview

import android.graphics.Bitmap
import android.os.AsyncTask
import com.olilay.golemreader.activities.OverviewActivity
import com.olilay.golemreader.models.MinimalArticle
import com.olilay.golemreader.parser.helper.AsyncTaskResult
import com.olilay.golemreader.parser.helper.ImageFetcher
import java.lang.ref.WeakReference
import java.lang.Exception

class ParseManager(activity: OverviewActivity) {
    var parsing: Boolean = false
    private var activity: WeakReference<OverviewActivity> = WeakReference(activity)
    private var minimalArticles: ArrayList<MinimalArticle> = ArrayList()

    private var expectedArticleAmount: Int

    init {
        this.expectedArticleAmount = -1
    }

    fun startParse() {
        if (!parsing) {
            parsing = true
            minimalArticles.clear()

            RssParser(this).parseAsync()
        }
    }

    fun onTickerParsed(tickerResult: Result<List<MinimalArticle>>) {
        if (tickerResult.isFailure) {
            onRefreshFailed(tickerResult.exceptionOrNull() as Exception)
        } else {
            val minimalArticles = tickerResult.getOrNull()

            minimalArticles!!.forEach { minimalArticle ->
                ImageFetcher(minimalArticle, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }

            if (minimalArticles.isEmpty()) {
                onEveryImageDownloaded()
            }

            expectedArticleAmount = minimalArticles.size
        }
    }

    fun onImageDownloaded(minimalArticle: MinimalArticle, bitmap: Bitmap) {
        minimalArticle.setArticleThumbnail(bitmap)
        minimalArticles.add(minimalArticle)

        if (minimalArticles.size >= expectedArticleAmount) {
            onEveryImageDownloaded()
        }
    }

    private fun onEveryImageDownloaded() {
        parsing = false
        expectedArticleAmount = -1
        minimalArticles.sortByDescending { a -> a.date }
        getOverviewActivity().onRefreshFinished(minimalArticles)
    }

    private fun onRefreshFailed(e: Exception) {
        parsing = false
        expectedArticleAmount = -1
        getOverviewActivity().onRefreshFailed(e)
    }

    private fun getOverviewActivity(): OverviewActivity {
        return activity.get()!!
    }
}