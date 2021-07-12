package com.olilay.golemreader.parser.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import kotlinx.coroutines.*
import java.io.InputStream
import java.lang.Exception
import java.net.URL

/**
 * Gets the image of an URL asynchronously.
 */
object ImageFetcher {
    fun forceGetAsync(url: URL) : Deferred<Bitmap> {
        return CoroutineScope(Dispatchers.IO).async {
            val result = downloadImage(url)
            result.getOrDefault(getDefaultBitmap())
        }
    }

    private fun downloadImage(url: URL): Result<Bitmap> {
        return try {
            Result.success(BitmapFactory.decodeStream(url.content as InputStream))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * @return A meaningless default [Bitmap].
     */
    fun getDefaultBitmap(): Bitmap {
        //TODO: use proper default image
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.BLACK)
        return bitmap
    }
}