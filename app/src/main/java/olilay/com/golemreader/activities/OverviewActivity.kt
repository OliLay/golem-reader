package olilay.com.golemreader.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import olilay.com.golemreader.R
import olilay.com.golemreader.parser.TickerParser
import android.support.v7.widget.Toolbar
import android.widget.Toast
import android.view.Menu
import android.view.MenuItem


class OverviewActivity : AppCompatActivity() {
    private var listView : ListView? = null

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        listView = findViewById(R.id.listview_news)

        try {
            val articles = TickerParser(this).execute().get()

            if (articles.isEmpty()) {
                System.out.println("No articles!")
            } else {
                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, articles)
                listView?.adapter = adapter
            }
        }
        catch (e : Exception) {
            System.out.println("Something went horrible wrong!!")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.action_refresh) {
            Toast.makeText(this, "Action clicked", Toast.LENGTH_LONG).show()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
