package com.olilay.golemreader.parser.overview

import android.graphics.Bitmap
import com.olilay.golemreader.activities.OverviewActivity
import com.olilay.golemreader.models.article.ArticleMetadata
import com.olilay.golemreader.parser.helper.ImageFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import java.lang.Exception

class TickerParseController(activity: OverviewActivity) {
    var parsing = false
    private var activity: WeakReference<OverviewActivity> = WeakReference(activity)
    private var articleMetadatas: ArrayList<ArticleMetadata> = ArrayList()
    private var rssParser = RssParser()

    private var expectedArticleAmount: Int

    init {
        this.expectedArticleAmount = -1
    }

    fun startParse() {
        if (!parsing) {
            parsing = true
            articleMetadatas.clear()

            CoroutineScope(Dispatchers.Main).launch {
                val minimalArticles = rssParser.parseAsync()
                onTickerParsed(minimalArticles)
            }
        }
    }

    private fun onTickerParsed(tickerResult: Result<List<ArticleMetadata>>) {
        if (tickerResult.isFailure) {
            onRefreshFailed(tickerResult.exceptionOrNull() as Exception)
        } else {
            val minimalArticles = tickerResult.getOrThrow()

            minimalArticles.forEach { minimalArticle ->
                if (minimalArticle.imageUrl == null) {
                    onImageDownloaded(minimalArticle, ImageFetcher.getDefaultBitmap())
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        val image = ImageFetcher.forceGetAsync(minimalArticle.imageUrl!!)
                        onImageDownloaded(minimalArticle, image)
                    }
                }
            }

            if (minimalArticles.isEmpty()) {
                onEveryImageDownloaded()
            }

            expectedArticleAmount = minimalArticles.size
        }
    }

    private fun onImageDownloaded(articleMetadata: ArticleMetadata, bitmap: Bitmap) {
        articleMetadata.setArticleThumbnail(bitmap)
        articleMetadatas.add(articleMetadata)

        if (articleMetadatas.size >= expectedArticleAmount) {
            onEveryImageDownloaded()
        }
    }

    private fun onEveryImageDownloaded() {
        parsing = false
        expectedArticleAmount = -1
        articleMetadatas.sortByDescending { a -> a.date }
        getOverviewActivity().onRefreshFinished(articleMetadatas)
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