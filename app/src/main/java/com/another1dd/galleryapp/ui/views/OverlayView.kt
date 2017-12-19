package com.another1dd.galleryapp.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.IntDef
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.utils.redactor.RectUtils
import com.another1dd.galleryapp.utils.redactor.callback.OverlayViewChangeListener
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy


class OverlayView : View {
    companion object {
        const val FREESTYLE_CROP_MODE_DISABLE = 0
        const val FREESTYLE_CROP_MODE_ENABLE = 1
        const val FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH = 2

        const val DEFAULT_SHOW_CROP_FRAME = true
        const val DEFAULT_SHOW_CROP_GRID = true
        const val DEFAULT_CIRCLE_DIMMED_LAYER = false
        const val DEFAULT_FREESTYLE_CROP_MODE = FREESTYLE_CROP_MODE_DISABLE
        const val DEFAULT_CROP_GRID_ROW_COUNT = 2
        const val DEFAULT_CROP_GRID_COLUMN_COUNT = 2
    }

    val cropViewRect = RectF()
    private val mTempRect = RectF()

    protected var mThisWidth: Int = 0
    protected var mThisHeight: Int = 0
    protected lateinit var mCropGridCorners: FloatArray
    protected lateinit var mCropGridCenter: FloatArray

    private var mCropGridRowCount: Int = 0
    private var mCropGridColumnCount: Int = 0
    private var mTargetAspectRatio: Float = 0.toFloat()
    private var mGridPoints: FloatArray? = null
    private var mShowCropFrame: Boolean = false
    private var mShowCropGrid: Boolean = false
    private var mCircleDimmedLayer: Boolean = false
    private var mDimmedColor: Int = 0
    private val mCircularPath = Path()
    private val mDimmedStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCropGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCropFramePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCropFrameCornersPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    @FreestyleMode
    private var mFreestyleCropMode = DEFAULT_FREESTYLE_CROP_MODE
    private var mPreviousTouchX = -1f
    private var mPreviousTouchY = -1f
    private var mCurrentTouchCornerIndex = -1
    private var mTouchPointThreshold: Int = 0
    private var mCropRectMinSize: Int = 0
    private var mCropRectCornerTouchAreaLineLength: Int = 0

    var overlayViewChangeListener: OverlayViewChangeListener? = null

    private var mShouldSetupCropBounds: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /***
     * Please use the new method [getFreestyleCropMode][.getFreestyleCropMode] method as we have more than 1 freestyle crop mode.
     */
    /***
     * Please use the new method [setFreestyleCropMode][.setFreestyleCropMode] method as we have more than 1 freestyle crop mode.
     */
    var isFreestyleCropEnabled: Boolean
        @Deprecated("")
        get() = mFreestyleCropMode == FREESTYLE_CROP_MODE_ENABLE
        @Deprecated("")
        set(freestyleCropEnabled) {
            mFreestyleCropMode = if (freestyleCropEnabled) FREESTYLE_CROP_MODE_ENABLE else FREESTYLE_CROP_MODE_DISABLE
        }

    var freestyleCropMode: Int
        @FreestyleMode
        get() = mFreestyleCropMode
        set(@FreestyleMode mFreestyleCropMode) {
            this.mFreestyleCropMode = mFreestyleCropMode
            postInvalidate()
        }

    init {
        mTouchPointThreshold = resources.getDimensionPixelSize(R.dimen.default_crop_rect_corner_touch_threshold)
        mCropRectMinSize = resources.getDimensionPixelSize(R.dimen.default_crop_rect_min_size)
        mCropRectCornerTouchAreaLineLength = resources.getDimensionPixelSize(R.dimen.default_crop_rect_corner_touch_area_line_length)
    }


    /**
     * Setter for [.mCircleDimmedLayer] variable.
     *
     * @param circleDimmedLayer - set it to true if you want dimmed layer to be an circle
     */
    fun setCircleDimmedLayer(circleDimmedLayer: Boolean) {
        mCircleDimmedLayer = circleDimmedLayer
    }

    /**
     * Setter for crop grid rows count.
     * Resets [.mGridPoints] variable because it is not valid anymore.
     */
    fun setCropGridRowCount(cropGridRowCount: Int) {
        mCropGridRowCount = cropGridRowCount
        mGridPoints = null
    }

    /**
     * Setter for crop grid columns count.
     * Resets [.mGridPoints] variable because it is not valid anymore.
     */
    fun setCropGridColumnCount(cropGridColumnCount: Int) {
        mCropGridColumnCount = cropGridColumnCount
        mGridPoints = null
    }

    /**
     * Setter for [.mShowCropFrame] variable.
     *
     * @param showCropFrame - set to true if you want to see a crop frame rectangle on top of an image
     */
    fun setShowCropFrame(showCropFrame: Boolean) {
        mShowCropFrame = showCropFrame
    }

    /**
     * Setter for [.mShowCropGrid] variable.
     *
     * @param showCropGrid - set to true if you want to see a crop grid on top of an image
     */
    fun setShowCropGrid(showCropGrid: Boolean) {
        mShowCropGrid = showCropGrid
    }

    /**
     * Setter for [.mDimmedColor] variable.
     *
     * @param dimmedColor - desired color of dimmed area around the crop bounds
     */
    fun setDimmedColor(@ColorInt dimmedColor: Int) {
        mDimmedColor = dimmedColor
    }

    /**
     * Setter for crop frame stroke width
     */
    fun setCropFrameStrokeWidth(width: Int) {
        mCropFramePaint.strokeWidth = width.toFloat()
    }

    /**
     * Setter for crop grid stroke width
     */
    fun setCropGridStrokeWidth(width: Int) {
        mCropGridPaint.strokeWidth = width.toFloat()
    }

    /**
     * Setter for crop frame color
     */
    fun setCropFrameColor(@ColorInt color: Int) {
        mCropFramePaint.color = color
    }

    /**
     * Setter for crop grid color
     */
    fun setCropGridColor(@ColorInt color: Int) {
        mCropGridPaint.color = color
    }

    /**
     * This method sets aspect ratio for crop bounds.
     *
     * @param targetAspectRatio - aspect ratio for image crop (e.g. 1.77(7) for 16:9)
     */
    fun setTargetAspectRatio(targetAspectRatio: Float) {
        mTargetAspectRatio = targetAspectRatio
        if (mThisWidth > 0) {
            setupCropBounds()
            postInvalidate()
        } else {
            mShouldSetupCropBounds = true
        }
    }

    /**
     * This method setups crop bounds rectangles for given aspect ratio and view size.
     * [.mCropViewRect] is used to draw crop bounds - uses padding.
     */
    fun setupCropBounds() {
        val height = (mThisWidth / mTargetAspectRatio).toInt()
        if (height > mThisHeight) {
            val width = (mThisHeight * mTargetAspectRatio).toInt()
            val halfDiff = (mThisWidth - width) / 2
            cropViewRect.set(paddingLeft.toFloat() + halfDiff, paddingTop.toFloat(),
                    paddingLeft.toFloat() + width + halfDiff, paddingTop.toFloat() + mThisHeight)
        } else {
            val halfDiff = (mThisHeight - height) / 2
            cropViewRect.set(paddingLeft.toFloat(), paddingTop.toFloat() + halfDiff,
                    paddingLeft.toFloat() + mThisWidth, paddingTop.toFloat() + height + halfDiff)
        }

        if (overlayViewChangeListener != null) {
            overlayViewChangeListener!!.onCropRectUpdated(cropViewRect)
        }

        updateGridPoints()
    }

    private fun updateGridPoints() {
        mCropGridCorners = RectUtils.getCornersFromRect(cropViewRect)
        mCropGridCenter = RectUtils.getCenterFromRect(cropViewRect)

        mGridPoints = null
        mCircularPath.reset()
        mCircularPath.addCircle(cropViewRect.centerX(), cropViewRect.centerY(),
                Math.min(cropViewRect.width(), cropViewRect.height()) / 2f, Path.Direction.CW)
    }

    init {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    protected override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var mLeft = left
        var mTop = top
        var mRight = right
        var mBottom = bottom
        super.onLayout(changed, mLeft, mTop, mRight, mBottom)
        if (changed) {
            mLeft = paddingLeft
            mTop = paddingTop
            mRight = width - paddingRight
            mBottom = height - paddingBottom
            mThisWidth = mRight - mLeft
            mThisHeight = mBottom - mTop

            if (mShouldSetupCropBounds) {
                mShouldSetupCropBounds = false
                setTargetAspectRatio(mTargetAspectRatio)
            }
        }
    }

    /**
     * Along with image there are dimmed layer, crop bounds and crop guidelines that must be drawn.
     */
    protected override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDimmedLayer(canvas)
        drawCropGrid(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (cropViewRect.isEmpty || mFreestyleCropMode == FREESTYLE_CROP_MODE_DISABLE) {
            return false
        }

        var x = event.x
        var y = event.y

        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN) {
            mCurrentTouchCornerIndex = getCurrentTouchIndex(x, y)
            val shouldHandle = mCurrentTouchCornerIndex != -1
            if (!shouldHandle) {
                mPreviousTouchX = -1f
                mPreviousTouchY = -1f
            } else if (mPreviousTouchX < 0) {
                mPreviousTouchX = x
                mPreviousTouchY = y
            }
            return shouldHandle
        }

        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_MOVE) {
            if (event.pointerCount == 1 && mCurrentTouchCornerIndex != -1) {

                x = Math.min(Math.max(x, paddingLeft.toFloat()), width - paddingRight.toFloat())
                y = Math.min(Math.max(y, paddingTop.toFloat()), height - paddingBottom.toFloat())

                updateCropViewRect(x, y)

                mPreviousTouchX = x
                mPreviousTouchY = y

                return true
            }
        }

        if (event.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_UP) {
            mPreviousTouchX = -1f
            mPreviousTouchY = -1f
            mCurrentTouchCornerIndex = -1

            if (overlayViewChangeListener != null) {
                overlayViewChangeListener!!.onCropRectUpdated(cropViewRect)
            }
        }

        return false
    }

    /**
     * * The order of the corners is:
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     */
    private fun updateCropViewRect(touchX: Float, touchY: Float) {
        mTempRect.set(cropViewRect)

        when (mCurrentTouchCornerIndex) {
        // resize rectangle
            0 -> mTempRect.set(touchX, touchY, cropViewRect.right, cropViewRect.bottom)
            1 -> mTempRect.set(cropViewRect.left, touchY, touchX, cropViewRect.bottom)
            2 -> mTempRect.set(cropViewRect.left, cropViewRect.top, touchX, touchY)
            3 -> mTempRect.set(touchX, cropViewRect.top, cropViewRect.right, touchY)
        // move rectangle
            4 -> {
                mTempRect.offset(touchX - mPreviousTouchX, touchY - mPreviousTouchY)
                if (mTempRect.left > left && mTempRect.top > top
                        && mTempRect.right < right && mTempRect.bottom < bottom) {
                    cropViewRect.set(mTempRect)
                    updateGridPoints()
                    postInvalidate()
                }
                return
            }
        }

        val changeHeight = mTempRect.height() >= mCropRectMinSize
        val changeWidth = mTempRect.width() >= mCropRectMinSize
        cropViewRect.set(
                if (changeWidth) mTempRect.left else cropViewRect.left,
                if (changeHeight) mTempRect.top else cropViewRect.top,
                if (changeWidth) mTempRect.right else cropViewRect.right,
                if (changeHeight) mTempRect.bottom else cropViewRect.bottom)

        if (changeHeight || changeWidth) {
            updateGridPoints()
            postInvalidate()
        }
    }

    /**
     * * The order of the corners in the float array is:
     * 0------->1
     * ^        |
     * |   4    |
     * |        v
     * 3<-------2
     *
     * @return - index of corner that is being dragged
     */
    private fun getCurrentTouchIndex(touchX: Float, touchY: Float): Int {
        var closestPointIndex = -1
        var closestPointDistance = mTouchPointThreshold.toDouble()
        var i = 0
        while (i < 8) {
            val distanceToCorner = Math.sqrt(Math.pow((touchX - mCropGridCorners[i]).toDouble(), 2.0) + Math.pow((touchY - mCropGridCorners[i + 1]).toDouble(), 2.0))
            if (distanceToCorner < closestPointDistance) {
                closestPointDistance = distanceToCorner
                closestPointIndex = i / 2
            }
            i += 2
        }

        return if (mFreestyleCropMode == FREESTYLE_CROP_MODE_ENABLE && closestPointIndex < 0 && cropViewRect.contains(touchX, touchY)) {
            4
        } else closestPointIndex

        //        for (int i = 0; i <= 8; i += 2) {
        //
        //            double distanceToCorner;
        //            if (i < 8) { // corners
        //                distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCorners[i], 2)
        //                        + Math.pow(touchY - mCropGridCorners[i + 1], 2));
        //            } else { // center
        //                distanceToCorner = Math.sqrt(Math.pow(touchX - mCropGridCenter[0], 2)
        //                        + Math.pow(touchY - mCropGridCenter[1], 2));
        //            }
        //            if (distanceToCorner < closestPointDistance) {
        //                closestPointDistance = distanceToCorner;
        //                closestPointIndex = i / 2;
        //            }
        //        }
    }

    /**
     * This method draws dimmed area around the crop bounds.
     *
     * @param canvas - valid canvas object
     */
    protected fun drawDimmedLayer(canvas: Canvas) {
        canvas.save()
        if (mCircleDimmedLayer) {
            canvas.clipPath(mCircularPath, Region.Op.DIFFERENCE)
        } else {
            canvas.clipRect(cropViewRect, Region.Op.DIFFERENCE)
        }
        canvas.drawColor(mDimmedColor)
        canvas.restore()

        if (mCircleDimmedLayer) { // Draw 1px stroke to fix antialias
            canvas.drawCircle(cropViewRect.centerX(), cropViewRect.centerY(),
                    Math.min(cropViewRect.width(), cropViewRect.height()) / 2f, mDimmedStrokePaint)
        }
    }

    /**
     * This method draws crop bounds (empty rectangle)
     * and crop guidelines (vertical and horizontal lines inside the crop bounds) if needed.
     *
     * @param canvas - valid canvas object
     */
    protected fun drawCropGrid(canvas: Canvas) {
        if (mShowCropGrid) {
            if (mGridPoints == null && !cropViewRect.isEmpty) {

                mGridPoints = FloatArray(mCropGridRowCount * 4 + mCropGridColumnCount * 4)

                var index = 0
                for (i in 0 until mCropGridRowCount) {
                    mGridPoints!![index++] = cropViewRect.left
                    mGridPoints!![index++] = cropViewRect.height() * ((i.toFloat() + 1.0f) / (mCropGridRowCount + 1).toFloat()) + cropViewRect.top
                    mGridPoints!![index++] = cropViewRect.right
                    mGridPoints!![index++] = cropViewRect.height() * ((i.toFloat() + 1.0f) / (mCropGridRowCount + 1).toFloat()) + cropViewRect.top
                }

                for (i in 0 until mCropGridColumnCount) {
                    mGridPoints!![index++] = cropViewRect.width() * ((i.toFloat() + 1.0f) / (mCropGridColumnCount + 1).toFloat()) + cropViewRect.left
                    mGridPoints!![index++] = cropViewRect.top
                    mGridPoints!![index++] = cropViewRect.width() * ((i.toFloat() + 1.0f) / (mCropGridColumnCount + 1).toFloat()) + cropViewRect.left
                    mGridPoints!![index++] = cropViewRect.bottom
                }
            }

            if (mGridPoints != null) {
                canvas.drawLines(mGridPoints, mCropGridPaint)
            }
        }

        if (mShowCropFrame) {
            canvas.drawRect(cropViewRect, mCropFramePaint)
        }

        if (mFreestyleCropMode != FREESTYLE_CROP_MODE_DISABLE) {
            canvas.save()

            mTempRect.set(cropViewRect)
            mTempRect.inset(mCropRectCornerTouchAreaLineLength.toFloat(), (-mCropRectCornerTouchAreaLineLength).toFloat())
            canvas.clipRect(mTempRect, Region.Op.DIFFERENCE)

            mTempRect.set(cropViewRect)
            mTempRect.inset((-mCropRectCornerTouchAreaLineLength).toFloat(), mCropRectCornerTouchAreaLineLength.toFloat())
            canvas.clipRect(mTempRect, Region.Op.DIFFERENCE)

            canvas.drawRect(cropViewRect, mCropFrameCornersPaint)

            canvas.restore()
        }
    }

    /**
     * This method extracts all needed values from the styled attributes.
     * Those are used to configure the view.
     */
    fun processStyledAttributes(a: TypedArray) {
        mCircleDimmedLayer = a.getBoolean(R.styleable.UCropView_circle_dimmed_layer, DEFAULT_CIRCLE_DIMMED_LAYER)
        mDimmedColor = a.getColor(R.styleable.UCropView_dimmed_color,
                resources.getColor(R.color.color_default_dimmed))
        mDimmedStrokePaint.color = mDimmedColor
        mDimmedStrokePaint.style = Paint.Style.STROKE
        mDimmedStrokePaint.strokeWidth = 1f

        initCropFrameStyle(a)
        mShowCropFrame = a.getBoolean(R.styleable.UCropView_show_frame, DEFAULT_SHOW_CROP_FRAME)

        initCropGridStyle(a)
        mShowCropGrid = a.getBoolean(R.styleable.UCropView_show_grid, DEFAULT_SHOW_CROP_GRID)
    }

    /**
     * This method setups Paint object for the crop bounds.
     */
    private fun initCropFrameStyle(a: TypedArray) {
        val cropFrameStrokeSize = a.getDimensionPixelSize(R.styleable.UCropView_frame_stroke_size,
                resources.getDimensionPixelSize(R.dimen.default_crop_frame_stoke_width))
        val cropFrameColor = a.getColor(R.styleable.UCropView_frame_color,
                resources.getColor(R.color.color_default_crop_frame))
        mCropFramePaint.strokeWidth = cropFrameStrokeSize.toFloat()
        mCropFramePaint.color = cropFrameColor
        mCropFramePaint.style = Paint.Style.STROKE

        mCropFrameCornersPaint.strokeWidth = cropFrameStrokeSize * 3f
        mCropFrameCornersPaint.color = cropFrameColor
        mCropFrameCornersPaint.style = Paint.Style.STROKE
    }

    /**
     * This method setups Paint object for the crop guidelines.
     */
    private fun initCropGridStyle(a: TypedArray) {
        val cropGridStrokeSize = a.getDimensionPixelSize(R.styleable.UCropView_grid_stroke_size,
                resources.getDimensionPixelSize(R.dimen.default_crop_grid_stoke_width))
        val cropGridColor = a.getColor(R.styleable.UCropView_grid_color,
                resources.getColor(R.color.color_default_crop_grid))
        mCropGridPaint.strokeWidth = cropGridStrokeSize.toFloat()
        mCropGridPaint.color = cropGridColor

        mCropGridRowCount = a.getInt(R.styleable.UCropView_grid_row_count, DEFAULT_CROP_GRID_ROW_COUNT)
        mCropGridColumnCount = a.getInt(R.styleable.UCropView_grid_column_count, DEFAULT_CROP_GRID_COLUMN_COUNT)
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef(FREESTYLE_CROP_MODE_DISABLE.toLong(), FREESTYLE_CROP_MODE_ENABLE.toLong(), FREESTYLE_CROP_MODE_ENABLE_WITH_PASS_THROUGH.toLong())
    annotation class FreestyleMode
}