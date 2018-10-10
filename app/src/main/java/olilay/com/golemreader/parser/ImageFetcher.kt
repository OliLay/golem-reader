package olilay.com.golemreader.parser

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import olilay.com.golemreader.R
import olilay.com.golemreader.models.MinimalArticle
import java.io.InputStream
import java.lang.Exception
import java.net.URL

/**
 * Gets the image of an url asynchronously.
 * @author Oliver Layer
 */
class ImageFetcher (private val minimalArticle: MinimalArticle,
                    private val parseManager : ParseManager) : AsyncTask<Void, Void, AsyncTaskResult<Bitmap>>() {

    override fun doInBackground(vararg void : Void) : AsyncTaskResult<Bitmap> {
        return try {
            AsyncTaskResult(downloadImage(minimalArticle.imageUrl))
        } catch (e : Exception) {
            Log.e("ImageFetcher", e.toString())
            AsyncTaskResult(getDefaultBitmap(parseManager.getOverviewActivity()), e)
        }
    }

    override fun onPostExecute(result: AsyncTaskResult<Bitmap>) {
        super.onPostExecute(result)

        parseManager.onImageDownloaded(minimalArticle, result.taskResult)
    }

    /**
     * Expects an URL in form of a String an tries to get the image in form of a [Bitmap]. If
     * it can not resolve the image behind the URL, it returns a default [Bitmap].
     * @return [Bitmap] of requested image.
     */
    private fun downloadImage(url : URL?) : Bitmap {
        url ?: return getDefaultBitmap(parseManager.getOverviewActivity())

        return try {
            BitmapFactory.decodeStream(url.content as InputStream)
        } catch (e: Exception) {
            getDefaultBitmap(parseManager.getOverviewActivity())
        }
    }

    /**
     * @return A meaningless default [Bitmap].
     */
    private fun getDefaultBitmap(activity: Activity): Bitmap {
        //TODO: use proper default image
        return BitmapFactory.decodeResource(activity.resources, R.drawable.tooltip_frame_dark)
    }
}