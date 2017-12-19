package com.another1dd.galleryapp.utils.redactor.callback

import android.graphics.RectF


interface OverlayViewChangeListener {
    fun onCropRectUpdated(cropRect: RectF)
}
