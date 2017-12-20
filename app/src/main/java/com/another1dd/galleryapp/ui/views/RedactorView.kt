package com.another1dd.galleryapp.ui.views

import android.content.Context
import android.graphics.RectF
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.utils.redactor.callback.CropBoundsChangeListener
import com.another1dd.galleryapp.utils.redactor.callback.OverlayViewChangeListener
import kotlinx.android.synthetic.main.redactor_view.view.*


class RedactorView : FrameLayout {
    var cropImageView: GestureCropImageView? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.redactor_view, this, true)

        cropImageView = imageViewCrop
        val a = context.obtainStyledAttributes(attrs, R.styleable.RedactorView)
        viewOverlay.processStyledAttributes(a)
        cropImageView?.processStyledAttributes(a)
        a.recycle()

        setListenersToViews()
    }

    private fun setListenersToViews() {
        cropImageView?.cropBoundsChangeListener = object : CropBoundsChangeListener {
            override fun onCropAspectRatioChanged(cropRatio: Float) {
                viewOverlay.setTargetAspectRatio(cropRatio)
            }
        }
        viewOverlay.overlayViewChangeListener = object : OverlayViewChangeListener {
            override fun onCropRectUpdated(cropRect: RectF) {
                cropImageView?.setCropRect(cropRect)
            }
        }
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    fun resetCropImageView(imageUri: Uri, outputUri: Uri) {
        removeView(cropImageView)
        cropImageView = GestureCropImageView(context)
        cropImageView?.setImageUri(imageUri, outputUri)
        cropImageView?.setCropRect(viewOverlay.cropViewRect)

        setListenersToViews()
        addView(cropImageView, 0)
    }
}
