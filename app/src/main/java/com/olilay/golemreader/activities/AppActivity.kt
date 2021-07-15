package com.olilay.golemreader.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.olilay.golemreader.R

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

    protected fun setViewVisibility(visible: Boolean, viewId: Int) {
        val view: View = findViewById(viewId)

        if (visible) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.INVISIBLE
        }
    }

    private fun setTextViewText(text: String, viewId: Int) {
        val view: TextView = findViewById(viewId)

        view.text = text
    }

    protected fun showErrorMessage(
        message: String,
        progressBarId: Int,
        errorImageId: Int,
        errorMessageId: Int
    ) {
        setViewVisibility(false, progressBarId)
        setViewVisibility(true, errorImageId)
        setViewVisibility(true, errorMessageId)

        setTextViewText(message, errorMessageId)
    }
}