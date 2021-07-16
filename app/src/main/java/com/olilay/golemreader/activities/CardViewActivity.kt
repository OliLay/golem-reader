package com.olilay.golemreader.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.olilay.golemreader.R

abstract class CardViewActivity(
    private val activityId: Int,
    private val refreshLayoutId: Int,
    private val recyclerViewId: Int
) : AppActivity() {
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var layoutManager: LinearLayoutManager
    protected lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(activityId)
        super.onCreate(savedInstanceState)

        refreshLayout = findViewById(refreshLayoutId)
        refreshLayout.setOnRefreshListener { refresh() }

        layoutManager = LinearLayoutManager(this)

        recyclerView = findViewById(recyclerViewId)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        setRecyclerViewScrollListener()

        refresh()
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
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {})
    }

    protected abstract fun refresh()
}