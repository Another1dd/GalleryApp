package com.another1dd.galleryapp.utils.redactor.callback

import android.graphics.Bitmap
import com.another1dd.galleryapp.models.redactor.ExifInfo


interface BitmapLoadCallback {
    fun onBitmapLoaded(bitmap: Bitmap, exifInfo: ExifInfo, imageInputPath: String, imageOutputPath: String)
    fun onFailure(bitmapWorkerException: Exception)
}
