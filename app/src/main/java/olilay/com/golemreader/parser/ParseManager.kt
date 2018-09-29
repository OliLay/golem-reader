package olilay.com.golemreader.parser

import android.os.AsyncTask
import olilay.com.golemreader.activities.OverviewActivity
import olilay.com.golemreader.models.Article
import olilay.com.golemreader.models.MinimalArticle
import org.jsoup.nodes.Element
import java.lang.ref.WeakReference

//TODO: feat: only load first X articles, load others when needed
//TODO: bug: parsing amount of comments sometimes fails
//TODO: feat: support 2 page articles
//TODO: feat: support slide shows (means not showing them :D )

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

            RssParser(this).execute()
            //TickerParser(this).execute()
        }
    }

    fun onTickerParsed(minimalArticles: List<MinimalArticle>) {
        minimalArticles.forEach { minimalArticle ->
            ArticleParser(minimalArticle, this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

        if (minimalArticles.isEmpty()) {
            onEveryArticleParsed()
        }

        expectedArticleAmount = minimalArticles.size
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
        activity!!.get()!!.onRefreshFinished(articles)
    }

    fun getOverviewActivity() : OverviewActivity {
        // Can not be null (passed with constructor),
        // so allow for NPEs
        return activity!!.get()!!
    }
}