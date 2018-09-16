package olilay.com.golemreader.parser

import android.app.Activity
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import olilay.com.golemreader.R
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.InputStream
import java.net.URL

object ParserUtils {
    /**
     * Connects to given URL, then downloads and parses it using Jsoup.
     * @return The parsed document.
     */
    fun getDocument(url: String) : Document {
        val con = Jsoup.connect(url)
        con.cookie("golem_view", "mobile") //ensure mobile view
        return con.get()
    }

    /**
     * Expects an URL in form of a String an tries to get the image in form of a Drawable. If
     * it can not resolve the image behind the URL, it returns a default Drawable.
     * @return The URL where the image is.
     */
    fun urlToDrawable(urlString: String?, activity : Activity) : Drawable {
        fun getDefaultDrawable() : Drawable {
            //TODO: use proper default image
            return ContextCompat.getDrawable(activity, R.drawable.tooltip_frame_dark)!!
        }

        if (urlString == null || urlString == "") {
            return getDefaultDrawable()
        }

        return try {
            Drawable.createFromStream(URL(urlString).content as InputStream, null)
        } catch (e : Exception) {
            getDefaultDrawable()
        }
    }
}