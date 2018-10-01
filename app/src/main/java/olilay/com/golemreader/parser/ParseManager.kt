package olilay.com.golemreader.parser

import android.os.AsyncTask
import olilay.com.golemreader.activities.OverviewActivity
import olilay.com.golemreader.models.Article
import olilay.com.golemreader.models.MinimalArticle
import java.lang.ref.WeakReference
import java.lang.Exception

//TODO: feat: only load first X articles, load others when needed
//TODO: feat: support 2 page articles

class ParseManager(activity: OverviewActivity) {
    var parsing : Boolean = false
    private var activity : WeakReference<OverviewActivity> = WeakReference(activity)
    private var articles : ArrayList<Article> = ArrayList()

    private var expectedArticleAmount : Int

    init {
        this.expectedArticleAmount = -1
    }

    fun startParse() {
        if (!parsing) {
            parsing = true
            articles.clear()

            RssParser(this).execute()
        }
    }

    fun onTickerParsed(taskResult: AsyncTaskResult<List<MinimalArticle>>) {
        val error = taskResult.error

        if (error != null) {
            onRefreshFailed(error)
        } else {
            val minimalArticles = taskResult.taskResult

            minimalArticles.forEach { minimalArticle ->
                ArticleParser(minimalArticle, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            }

            if (minimalArticles.isEmpty()) {
                onEveryArticleParsed()
            }

            expectedArticleAmount = minimalArticles.size
        }
    }

    fun onArticleParsed(article: Article) {
        articles.add(article)

        if (articles.size >= expectedArticleAmount) {
            onEveryArticleParsed()
        }
    }

    private fun onEveryArticleParsed() {
        parsing = false
        expectedArticleAmount = -1
        articles.sortByDescending { a -> a.date }
        getOverviewActivity().onRefreshFinished(articles)
    }

    private fun onRefreshFailed(e: Exception) {
        parsing = false
        expectedArticleAmount = -1
        getOverviewActivity().onRefreshFailed(e)
    }

    fun getOverviewActivity() : OverviewActivity {
        return activity.get()!!
    }
}