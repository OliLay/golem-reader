package com.olilay.golemreader.models.comment

import android.graphics.Bitmap
import android.os.Parcelable
import android.text.format.DateUtils
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
open class CommentMetadata(
    open var heading: String,
    open var url: URL,
    open var author: String,
    open var dateCreated: Date,
) : Parcelable {

    fun getCreatedDateString(): String {
        /*
        as golem.de offers only articles in German language, we also display the
        date info using the German locale
         */
        Locale.setDefault(Locale.GERMANY)
        return DateUtils.getRelativeTimeSpanString(
            dateCreated.time, Date().time, DateUtils.DAY_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_WEEKDAY
        ).toString()
    }

    fun getCreatedTimeString(): String {
        return SimpleDateFormat("HH:mm", Locale.GERMAN).format(dateCreated) + " Uhr"
    }

}