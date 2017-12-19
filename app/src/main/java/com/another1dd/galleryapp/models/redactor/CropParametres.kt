package com.another1dd.galleryapp.models.redactor

import android.graphics.Bitmap


data class CropParametres(
        var mMaxResultImageSizeX: Int = 0,
        var mMaxResultImageSizeY: Int = 0,

        var mCompressFormat: Bitmap.CompressFormat? = null,
        var mCompressQuality: Int = 0,
        var mImageInputPath: String? = null,
        var mImageOutputPath: String? = null,
        var mExifInfo: ExifInfo? = null)
