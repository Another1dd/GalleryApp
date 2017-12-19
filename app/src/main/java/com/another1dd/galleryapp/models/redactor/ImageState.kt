package com.another1dd.galleryapp.models.redactor

import android.graphics.RectF


data class ImageState(
        var mCropRect: RectF? = null,
        var mCurrentImageRect: RectF? = null,

        var mCurrentScale: Float = 0f,
        var mCurrentAngle: Float = 0f
)
