package olilay.com.golemreader.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import olilay.com.golemreader.R

/**
 * Use this activity class for every activity in the app as super class.
 * Sets Toolbar.
 */
abstract class AppActivity() : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }
}