package olilay.com.golemreader.models

import android.graphics.drawable.Drawable
import java.net.URL
import java.util.*

data class Article (val heading : String,
                    val url : URL,
                    val description : String,
                    val thumbnail : Drawable,
                    val date : Date,
                    val author : String,
                    val amountOfComments : Int)