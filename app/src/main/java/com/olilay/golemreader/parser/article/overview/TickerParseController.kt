package com.olilay.golemreader.parser.article.overview

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
                val articleMetadatas = rssParser.parseAsync()
                onTickerParsed(articleMetadatas)
            }
        }
    }

    private fun onTickerParsed(tickerResult: Result<List<ArticleMetadata>>) {
        if (tickerResult.isFailure) {
            onRefreshFailed(tickerResult.exceptionOrNull() as Exception)
        } else {
            val articleMetadatas = tickerResult.getOrThrow()

            articleMetadatas.forEach { articleMetadata ->
                if (articleMetadata.imageUrl == null) {
                    onImageDownloaded(articleMetadata, ImageFetcher.getDefaultBitmap())
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        val image = ImageFetcher.forceGetAsync(articleMetadata.imageUrl!!)
                        onImageDownloaded(articleMetadata, image)
                    }
                }
            }

            if (articleMetadatas.isEmpty()) {
                onEveryImageDownloaded()
            }

            expectedArticleAmount = articleMetadatas.size
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