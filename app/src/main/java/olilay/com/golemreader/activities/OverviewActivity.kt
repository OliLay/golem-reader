package olilay.com.golemreader.activities

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import olilay.com.golemreader.R
import olilay.com.golemreader.parser.TickerParser
import android.widget.Toast
import android.view.Menu
import android.view.MenuItem
import android.view.View
import olilay.com.golemreader.adapter.ArticleAdapter


class OverviewActivity : AppActivity() {
    private var recyclerView: RecyclerView? = null
    private var viewManager: RecyclerView.LayoutManager? = null
    private var tickerParser : TickerParser? = null
    private var refreshing : Boolean = false

    override fun onCreate(savedInstanceState : Bundle?) {
        setContentView(R.layout.activity_overview)
        super.onCreate(savedInstanceState)


        //CARD VIEW
        viewManager = LinearLayoutManager(this)

        recyclerView = findViewById<RecyclerView>(R.id.overview_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }

        refresh()
    }

    private fun refresh() {
        if (!refreshing) {
            refreshing = true
            setViewVisiblity(true, R.id.overview_progress_bar)
            setViewVisiblity(false, R.id.overview_recycler_view)

            tickerParser = TickerParser(this)
            tickerParser?.execute()
                    ?: throw NullPointerException("TickerParser is null when trying to refresh!")
        }
    }

    fun refreshFinished() {
        refreshing = false
        setViewVisiblity(false, R.id.overview_progress_bar)
        setViewVisiblity(true, R.id.overview_recycler_view)
        try {
            val articles = tickerParser!!.get()

            if (articles.isEmpty()) {
                System.out.println("No articles!")
            } else {
                recyclerView?.adapter = ArticleAdapter(articles)
            }
        } catch (e : Exception) {
            Toast.makeText(this, "Something went horrible wrong!!", Toast.LENGTH_LONG).show()
        }
    }

    private fun setViewVisiblity(visible: Boolean, viewId: Int) {
        val view : View = findViewById(viewId)

        if (visible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_refresh) {
            refresh()
        }

        return super.onOptionsItemSelected(item)
    }
}
