package olilay.com.golemreader.models

import android.graphics.drawable.Drawable
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

data class Article (val preHeading : String,
                    val heading : String,
                    val url : URL,
                    val description : String,
                    val thumbnail : Drawable,
                    val date : Date,
                    val author : String,
                    val amountOfComments : Int,
                    val content : String) {

    fun getFullHeading() : String {
        return "$preHeading: $heading"
    }

    fun getDateString() : String {
        val df = SimpleDateFormat("E, d.M", Locale.GERMANY)
        return df.format(date)
    }

    fun getTimeString() : String {
        val df = SimpleDateFormat("HH:mm", Locale.GERMANY)
        return df.format(date) + " Uhr"
    }
}