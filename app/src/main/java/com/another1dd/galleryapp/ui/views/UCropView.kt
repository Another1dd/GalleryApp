package com.another1dd.galleryapp.ui.views

import android.content.Context
import android.graphics.RectF
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.utils.redactor.callback.CropBoundsChangeListener
import com.another1dd.galleryapp.utils.redactor.callback.OverlayViewChangeListener
import kotlinx.android.synthetic.main.crop_view.view.*


class UCropView : FrameLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.crop_view, this, true)

        val a = context.obtainStyledAttributes(attrs, R.styleable.UCropView)
        viewOverlay.processStyledAttributes(a)
        imageViewCrop.processStyledAttributes(a)
        a.recycle()


        setListenersToViews()
    }

    private fun setListenersToViews() {
        imageViewCrop.cropBoundsChangeListener = object : CropBoundsChangeListener {
            override fun onCropAspectRatioChanged(cropRatio: Float) {
                viewOverlay.setTargetAspectRatio(cropRatio)
            }
        }
        viewOverlay.overlayViewChangeListener = object : OverlayViewChangeListener {
            override fun onCropRectUpdated(cropRect: RectF) {
                imageViewCrop.setCropRect(cropRect)
            }
        }
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    /**
     * Method for reset state for UCropImageView such as rotation, scale, translation.
     * Be careful: this method recreate UCropImageView instance and reattach it to layout.
     */
//    fun resetCropImageView() {
//        removeView(imageViewCrop)
//        imageViewCrop = GestureCropImageView(context)
//        setListenersToViews()
//        imageViewCrop.setCropRect(viewOverlay.cropViewRect)
//        addView(imageViewCrop, 0)
//    }
}
