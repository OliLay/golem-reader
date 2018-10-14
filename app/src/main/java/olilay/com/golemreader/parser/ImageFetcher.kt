package olilay.com.golemreader.parser

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.util.Log
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
            AsyncTaskResult(getDefaultBitmap(), e)
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
        url ?: return getDefaultBitmap()

        return try {
            BitmapFactory.decodeStream(url.content as InputStream)
        } catch (e: Exception) {
            getDefaultBitmap()
        }
    }

    /**
     * @return A meaningless default [Bitmap].
     */
    private fun getDefaultBitmap(): Bitmap {
        //TODO: use proper default image
        val bitmap =  Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.BLACK)
        return bitmap
    }
}