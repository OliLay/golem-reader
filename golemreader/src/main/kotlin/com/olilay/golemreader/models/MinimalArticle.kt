package com.olilay.golemreader.models

import android.graphics.Bitmap
import android.os.Parcelable
import android.text.format.DateUtils
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
open class MinimalArticle(var heading: String,
                          var url: URL,
                          var description: String,
                          var date: Date,
                          var imageUrl: URL?,
                          var amountOfComments: Int,
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