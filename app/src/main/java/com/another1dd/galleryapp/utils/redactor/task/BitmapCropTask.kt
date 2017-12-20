package com.another1dd.galleryapp.utils.redactor.task

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RectF
import android.media.ExifInterface
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import com.another1dd.galleryapp.models.redactor.CropParametres
import com.another1dd.galleryapp.models.redactor.ImageState
import com.another1dd.galleryapp.utils.redactor.FileUtils
import com.another1dd.galleryapp.utils.redactor.ImageHeaderParser
import com.another1dd.galleryapp.utils.redactor.callback.BitmapCropCallback
import java.io.File
import java.io.IOException

class BitmapCropTask(private var mViewBitmap: Bitmap?, imageState: ImageState, cropParameters: CropParametres,
                     private val mCropCallback: BitmapCropCallback?) : AsyncTask<Void, Void, Throwable>() {

    private val mCropRect: RectF? = imageState.mCropRect
    private val mCurrentImageRect: RectF? = imageState.mCurrentImageRect

    private var mCurrentScale = imageState.mCurrentScale
    private val mCurrentAngle = imageState.mCurrentAngle
    private val mMaxResultImageSizeX = cropParameters.mMaxResultImageSizeX
    private val mMaxResultImageSizeY = cropParameters.mMaxResultImageSizeY

    private var mCompressFormat = cropParameters.mCompressFormat
    private val mCompressQuality: Int = cropParameters.mCompressQuality
    private val mImageInputPath: String? = cropParameters.mImageInputPath
    private val mImageOutputPath: String? = cropParameters.mImageOutputPath

    private var mCroppedImageWidth: Int = 0
    private var mCroppedImageHeight: Int = 0
    private var cropOffsetX: Int = 0
    private var cropOffsetY: Int = 0


    override fun doInBackground(vararg params: Void): Throwable? {
        when {
            mViewBitmap == null -> return NullPointerException("ViewBitmap is null")
            mViewBitmap!!.isRecycled -> return NullPointerException("ViewBitmap is recycled")
            mCurrentImageRect!!.isEmpty -> return NullPointerException("CurrentImageRect is empty")
            else -> {
                val resizeScale = resize()

                try {
                    crop(resizeScale)

                    mViewBitmap = null
                } catch (throwable: Throwable) {
                    return throwable
                }

                return null
            }
        }

    }

    private fun resize(): Float {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mImageInputPath, options)


        var scaleX = (options.outWidth) / mViewBitmap!!.width.toFloat()
        var scaleY = (options.outHeight) / mViewBitmap!!.height.toFloat()

        var resizeScale = Math.min(scaleX, scaleY)

        mCurrentScale /= resizeScale

        resizeScale = 1f
        if (mMaxResultImageSizeX > 0 && mMaxResultImageSizeY > 0) {
            val cropWidth = mCropRect!!.width() / mCurrentScale
            val cropHeight = mCropRect.height() / mCurrentScale

            if (cropWidth > mMaxResultImageSizeX || cropHeight > mMaxResultImageSizeY) {

                scaleX = mMaxResultImageSizeX / cropWidth
                scaleY = mMaxResultImageSizeY / cropHeight
                resizeScale = Math.min(scaleX, scaleY)

                mCurrentScale /= resizeScale
            }
        }
        return resizeScale
    }

    @Throws(IOException::class)
    private fun crop(resizeScale: Float): Boolean {
        val originalExif = ExifInterface(mImageInputPath)

        cropOffsetX = Math.round((mCropRect!!.left - mCurrentImageRect!!.left) / mCurrentScale)
        cropOffsetY = Math.round((mCropRect.top - mCurrentImageRect.top) / mCurrentScale)
        mCroppedImageWidth = Math.round(mCropRect.width() / mCurrentScale)
        mCroppedImageHeight = Math.round(mCropRect.height() / mCurrentScale)

        val shouldCrop = shouldCrop(mCroppedImageWidth, mCroppedImageHeight)
        Log.i(TAG, "Should crop: " + shouldCrop)

        return if (shouldCrop) {
            val cropped = cropCImg(mImageInputPath!!, mImageOutputPath!!,
                    cropOffsetX, cropOffsetY, mCroppedImageWidth, mCroppedImageHeight,
                    mCurrentAngle, resizeScale, mCompressFormat!!.ordinal, mCompressQuality)
            if (cropped && mCompressFormat == Bitmap.CompressFormat.JPEG) {
                ImageHeaderParser.copyExif(originalExif, mCroppedImageWidth, mCroppedImageHeight, mImageOutputPath)
            }
            cropped
        } else {
            FileUtils.copyFile(mImageInputPath!!, mImageOutputPath!!)
            false
        }
    }

    /**
     * Check whether an image should be cropped at all or just file can be copied to the destination path.
     * For each 1000 pixels there is one pixel of error due to matrix calculations etc.
     *
     * @param width  - crop area width
     * @param height - crop area height
     * @return - true if image must be cropped, false - if original image fits requirements
     */
    private fun shouldCrop(width: Int, height: Int): Boolean {
        var pixelError = 1
        pixelError += Math.round(Math.max(width, height) / 1000f)
        return (mMaxResultImageSizeX > 0 && mMaxResultImageSizeY > 0
                || Math.abs(mCropRect!!.left - mCurrentImageRect!!.left) > pixelError
                || Math.abs(mCropRect.top - mCurrentImageRect.top) > pixelError
                || Math.abs(mCropRect.bottom - mCurrentImageRect.bottom) > pixelError
                || Math.abs(mCropRect.right - mCurrentImageRect.right) > pixelError
                || mCurrentAngle != 0f)
    }

    override fun onPostExecute(t: Throwable?) {
        if (mCropCallback != null) {
            if (t == null) {
                val uri = Uri.fromFile(File(mImageOutputPath))
                mCropCallback.onBitmapCropped(uri, cropOffsetX, cropOffsetY, mCroppedImageWidth, mCroppedImageHeight)
            } else {
                mCropCallback.onCropFailure(t)
            }
        }
    }

    companion object {

        private val TAG = "BitmapCropTask"

        init {
            System.loadLibrary("ucrop")
        }

        @Throws(IOException::class, OutOfMemoryError::class)
        external fun cropCImg(inputPath: String, outputPath: String,
                              left: Int, top: Int, width: Int, height: Int,
                              angle: Float, resizeScale: Float,
                              format: Int, quality: Int): Boolean
    }

}