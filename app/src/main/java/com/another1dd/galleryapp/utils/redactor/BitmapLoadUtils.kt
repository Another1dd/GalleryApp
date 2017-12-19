package com.another1dd.galleryapp.utils.redactor

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.view.WindowManager
import com.another1dd.galleryapp.utils.redactor.callback.BitmapLoadCallback
import com.another1dd.galleryapp.utils.redactor.task.BitmapLoadTask
import java.io.Closeable
import java.io.IOException


object BitmapLoadUtils {
    private val TAG = "BitmapLoadUtils"

    fun decodeBitmapInBackground(context: Context,
                                  uri: Uri,  outputUri: Uri,
                                 requiredWidth: Int, requiredHeight: Int,
                                 loadCallback: BitmapLoadCallback) {
        BitmapLoadTask(context, uri, outputUri, requiredWidth, requiredHeight, loadCallback).execute()
    }

    fun transformBitmap(bitmap: Bitmap, transformMatrix: Matrix): Bitmap {
        var newBitmap = bitmap
        try {
            val converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, transformMatrix, true)
            if (!newBitmap.sameAs(converted)) {
                newBitmap = converted
            }
        } catch (error: OutOfMemoryError) {
            Log.e(TAG, "transformBitmap: ", error)
        }

        return newBitmap
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width lower or equal to the requested height and width.
            while (height / inSampleSize > reqHeight || width / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun getExifOrientation(context: Context, imageUri: Uri): Int {
        var orientation = ExifInterface.ORIENTATION_UNDEFINED
        try {
            val stream = context.contentResolver.openInputStream(imageUri) ?: return orientation
            orientation = ImageHeaderParser(stream).orientation
            close(stream)
        } catch (e: IOException) {
            Log.e(TAG, "getExifOrientation: " + imageUri.toString(), e)
        }

        return orientation
    }

    fun exifToDegrees(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_ROTATE_90, ExifInterface.ORIENTATION_TRANSPOSE -> 90
            ExifInterface.ORIENTATION_ROTATE_180, ExifInterface.ORIENTATION_FLIP_VERTICAL -> 180
            ExifInterface.ORIENTATION_ROTATE_270, ExifInterface.ORIENTATION_TRANSVERSE -> 270
            else -> 0
        }
    }

    fun exifToTranslation(exifOrientation: Int): Int {
        return when (exifOrientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL, ExifInterface.ORIENTATION_FLIP_VERTICAL, ExifInterface.ORIENTATION_TRANSPOSE, ExifInterface.ORIENTATION_TRANSVERSE -> -1
            else -> 1
        }
    }

    /**
     * This method calculates maximum size of both width and height of bitmap.
     * It is twice the device screen diagonal for default implementation (extra quality to zoom image).
     * Size cannot exceed max texture size.
     *
     * @return - max bitmap size in pixels.
     */
    fun calculateMaxBitmapSize(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay

        val size = Point()
        val width: Int
        val height: Int
        display.getSize(size)
        width = size.x
        height = size.y

        // Twice the device screen diagonal as default
        var maxBitmapSize = Math.sqrt(Math.pow(width.toDouble(), 2.0) + Math.pow(height.toDouble(), 2.0)).toInt()

        // Check for max texture size via Canvas
        val canvas = Canvas()
        val maxCanvasSize = Math.min(canvas.maximumBitmapWidth, canvas.maximumBitmapHeight)
        if (maxCanvasSize > 0) {
            maxBitmapSize = Math.min(maxBitmapSize, maxCanvasSize)
        }

        // Check for max texture size via GL
        val maxTextureSize = EglUtils.maxTextureSize
        if (maxTextureSize > 0) {
            maxBitmapSize = Math.min(maxBitmapSize, maxTextureSize)
        }

        Log.d(TAG, "maxBitmapSize: " + maxBitmapSize)
        return maxBitmapSize
    }

    fun close(c: Closeable?) {
        if (c != null) { // java.lang.IncompatibleClassChangeError: interface not implemented
            try {
                c.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}
