package olilay.com.golemreader.activities

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import olilay.com.golemreader.R
import android.view.Menu
import android.view.MenuItem
import olilay.com.golemreader.adapter.ArticleAdapter
import olilay.com.golemreader.models.Article
import olilay.com.golemreader.parser.ParseManager
import java.lang.Exception


class OverviewActivity : AppActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var parseManager : ParseManager

    override fun onCreate(savedInstanceState : Bundle?) {
        setContentView(R.layout.activity_overview)
        super.onCreate(savedInstanceState)

        //SWIPE REFRESH LAYOUT
        refreshLayout = findViewById(R.id.overview_swiperefresh);
        refreshLayout.setOnRefreshListener{ refresh() }

        //CARD VIEW
        layoutManager = LinearLayoutManager(this)

        recyclerView = findViewById(R.id.overview_recycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        setRecyclerViewScrollListener()

        //PARSE MANAGER
        parseManager = ParseManager(this)

        refresh()
    }

    private fun refresh() {
        refreshLayout.isRefreshing = false //make SwipeRefreshLayout loading animation disappear

        if (!parseManager.parsing) {
            setViewVisibility(true, R.id.overview_progress_bar)
            setViewVisibility(false, R.id.overview_recycler_view)
            setViewVisibility(false,  R.id.overview_error_image)
            setViewVisibility(false,  R.id.overview_error_message)

            parseManager.startParse()
        }
    }

    fun onRefreshFinished(articles : List<Article>) {
        setViewVisibility(false, R.id.overview_progress_bar)
        setViewVisibility(true, R.id.overview_recycler_view)

        recyclerView.adapter = ArticleAdapter(articles)
    }

    fun onRefreshFailed(e: Exception) {
        setViewVisibility(false, R.id.overview_progress_bar)
        setViewVisibility(true, R.id.overview_error_image)
        setViewVisibility(true, R.id.overview_error_message)

        val message = when (e) {
            is java.net.UnknownHostException -> resources.getString(R.string.no_connection)
            else -> resources.getString(R.string.error)
        }

        setTextViewText(message, R.id.overview_error_message)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_refresh) {
            refresh()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun setRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
    }
}
