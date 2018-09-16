package olilay.com.golemreader.models

import android.graphics.drawable.Drawable
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

data class Article (var preHeading : String,
                    var heading : String,
                    var url : URL,
                    var description : String,
                    var thumbnail : Drawable,
                    var date : Date,
                    var author : String,
                    var amountOfComments : Int,
                    var content : String) {


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