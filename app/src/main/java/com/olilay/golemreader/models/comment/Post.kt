package com.olilay.golemreader.models.comment

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

class Post(
    var author: String,
    var heading: String,
    var date: Date,
    var content: String
) {

    private fun getDateString(): String {
        /*
        as golem.de offers only articles in German language, we also display the
        date info using the German locale
         */
        Locale.setDefault(Locale.GERMANY)
        return DateUtils.getRelativeTimeSpanString(
            date.time, Date().time, DateUtils.DAY_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_WEEKDAY
        ).toString()
    }

    private fun getTimeString(): String {
        return SimpleDateFormat("HH:mm", Locale.GERMAN).format(date) + " Uhr"
    }

    fun getDateTimeString(): String {
        return "${getDateString()}, ${getTimeString()}"
    }

}