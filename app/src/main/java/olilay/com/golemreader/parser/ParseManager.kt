package olilay.com.golemreader.parser

import android.os.AsyncTask
import olilay.com.golemreader.activities.OverviewActivity
import olilay.com.golemreader.models.Article
import org.jsoup.nodes.Element
import java.lang.ref.WeakReference

class ParseManager(activity: OverviewActivity) {
    var parsing : Boolean = false
    private var activity : WeakReference<OverviewActivity>? = null
    private var articles : ArrayList<Article>

    private var expectedArticleAmount : Int

    init {
        this.activity = WeakReference(activity)
        this.articles = ArrayList()
        this.expectedArticleAmount = -1
    }

    fun startParse() {
        if (!parsing) {
            parsing = true
            articles.clear()

            TickerParser(this).execute()
        }
    }

    fun onTickerParsed(elems : List<Element>) {
        elems.forEach { elem ->
            ArticleParser(elem, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

        expectedArticleAmount = elems.size
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
        activity!!.get()!!.onRefreshFinished(articles)
    }

    fun getOverviewActivity() : OverviewActivity {
        // Can not be null (passed with constructor),
        // so allow for NPEs
        return activity!!.get()!!
    }
}