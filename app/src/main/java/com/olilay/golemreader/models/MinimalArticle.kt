package com.olilay.golemreader.models

import android.graphics.Bitmap
import android.os.Parcelable
import android.text.format.DateUtils
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
open class MinimalArticle(open var heading: String,
                          open var url: URL,
                          open var description: String,
                          open var date: Date,
                          open val imageUrl: URL?,
                          open var amountOfComments: Int,
                          var thumbnail: Bitmap? = null) : Parcelable {

    @IgnoredOnParcel
    private var isThumbnailDownloaded = false


    fun getDateString(): String {
        return DateUtils.getRelativeTimeSpanString(date.time, Date().time, DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_WEEKDAY).toString()
    }

    fun getTimeString(): String {
        val df = SimpleDateFormat("HH:mm", Locale.GERMANY)
        return df.format(date) + " Uhr"
    }

    fun setArticleThumbnail(bitmap: Bitmap) {
        thumbnail = bitmap
        isThumbnailDownloaded = true
    }
}