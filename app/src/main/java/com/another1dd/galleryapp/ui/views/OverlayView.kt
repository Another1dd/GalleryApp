package com.another1dd.galleryapp.ui.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.another1dd.galleryapp.R
import com.another1dd.galleryapp.models.constants.DivideType
import com.another1dd.galleryapp.utils.redactor.RectUtils
import com.another1dd.galleryapp.utils.redactor.callback.OverlayViewChangeListener


open class OverlayView : View {
    companion object {
        const val DEFAULT_SHOW_CROP_GRID = true
        const val DEFAULT_SHOW_DIVIDER_GRID = false
        const val DEFAULT_CROP_GRID_ROW_COUNT = 2
        const val DEFAULT_CROP_GRID_COLUMN_COUNT = 2
        const val DEFAULT_DIVIDER_GRID_ROW_COUNT = 2
        const val DEFAULT_DIVIDER_GRID_COLUMN_COUNT = 2
    }

    val cropViewRect = RectF()
    private val dividerViewRect = RectF()
    private val mTempRect = RectF()

    private var mThisWidth: Int = 0
    private var mThisHeight: Int = 0
    private lateinit var mCropGridCorners: FloatArray
    private lateinit var mCropGridCenter: FloatArray
    private lateinit var mDividerGridCorners: FloatArray
    private lateinit var mDividerGridCenter: FloatArray

    private var mCropGridRowCount: Int = 0
    private var mCropGridColumnCount: Int = 0
    private var mDividerGridRowCount: Int = 0
    private var mDividerGridColumnCount: Int = 0
    private var mTargetAspectRatio = 0f
    private var mGridPoints: FloatArray? = null
    private var mDividerGridPoints: FloatArray? = null
    private var mShowCropGrid: Boolean = false
    private var mShowDividerGrid: Boolean = false
    private var mDividerType: Int = DivideType.NO_DIVIDE
    private var mDimmedColor: Int = 0
    private val mDimmedStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCropGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mDividerGridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mDividerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPreviousTouchX = -1f
    private var mPreviousTouchY = -1f
    private var mCurrentTouchCornerIndex = -1
    private var mTouchPointThreshold: Int = 0
    private var mCropRectMinSize: Int = 0
    private var mCropRectCornerTouchAreaLineLength: Int = 0

    var overlayViewChangeListener: OverlayViewChangeListener? = null

    private var mShouldSetupCropAndDividerBounds: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        mTouchPointThreshold = resources.getDimensionPixelSize(R.dimen.default_crop_rect_corner_touch_threshold)
        mCropRectMinSize = resources.getDimensionPixelSize(R.dimen.default_crop_rect_min_size)
        mCropRectCornerTouchAreaLineLength = resources.getDimensionPixelSize(R.dimen.default_crop_rect_corner_touch_area_line_length)
    }


    fun setShowDividerGrid(show: Boolean) {
        mShowDividerGrid = show
        mDividerType = DivideType.NO_DIVIDE
        postInvalidate()
    }

    fun setDividerType(dividerType: Int) {
        mShowDividerGrid = true
        mDividerType = dividerType
        postInvalidate()
    }

    /**
     * This method sets aspect ratio for crop bounds.
     *
     * @param targetAspectRatio - aspect ratio for image crop (e.g. 1.77(7) for 16:9)
     */
    fun setTargetAspectRatio(targetAspectRatio: Float) {
        mTargetAspectRatio = targetAspectRatio
        if (mThisWidth > 0) {
            setupCropAndDividerBounds()
            postInvalidate()
        } else {
            mShouldSetupCropAndDividerBounds = true
        }
    }

    /**
     * This method setups crop bounds rectangles for given aspect ratio and view size.
     * [.mCropViewRect] is used to draw crop bounds - uses padding.
     */
    private fun setupCropAndDividerBounds() {
        val height = (mThisWidth / mTargetAspectRatio).toInt()
        if (height > mThisHeight) {
            val width = (mThisHeight * mTargetAspectRatio).toInt()
            val halfDiff = (mThisWidth - width) / 2
            cropViewRect.set(paddingLeft.toFloat() + halfDiff, paddingTop.toFloat(),
                    paddingLeft.toFloat() + width + halfDiff, paddingTop.toFloat() + mThisHeight)
            dividerViewRect.set(paddingLeft.toFloat() + halfDiff, paddingTop.toFloat(),
                    paddingLeft.toFloat() + width + halfDiff, paddingTop.toFloat() + mThisHeight)
        } else {
            val halfDiff = (mThisHeight - height) / 2
            cropViewRect.set(paddingLeft.toFloat(), paddingTop.toFloat() + halfDiff,
                    paddingLeft.toFloat() + mThisWidth, paddingTop.toFloat() + height + halfDiff)
            dividerViewRect.set(paddingLeft.toFloat(), paddingTop.toFloat() + halfDiff,
                    paddingLeft.toFloat() + mThisWidth, paddingTop.toFloat() + height + halfDiff)
        }

        if (overlayViewChangeListener != null) {
            overlayViewChangeListener?.onCropRectUpdated(cropViewRect)
        }

        updateGridPoints()
        updateDividerGridPoints()
    }

    private fun updateGridPoints() {
        mCropGridCorners = RectUtils.getCornersFromRect(cropViewRect)
        mCropGridCenter = RectUtils.getCenterFromRect(cropViewRect)

        mGridPoints = null
    }

    private fun updateDividerGridPoints() {
        mDividerGridCorners = RectUtils.getCornersFromRect(dividerViewRect)
        mDividerGridCenter = RectUtils.getCenterFromRect(dividerViewRect)

        mDividerGridPoints = null
    }

    init {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
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

            if (mShouldSetupCropAndDividerBounds) {
                mShouldSetupCropAndDividerBounds = false
                setTargetAspectRatio(mTargetAspectRatio)
            }
        }
    }

    /**
     * Along with image there are dimmed layer, crop bounds and crop guidelines that must be drawn.
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawDimmedLayer(canvas)
        drawCropGrid(canvas)
        drawDividerGrid(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (cropViewRect.isEmpty) {
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

        return closestPointIndex
    }

    /**
     * This method draws dimmed area around the crop bounds.
     *
     * @param canvas - valid canvas object
     */
    private fun drawDimmedLayer(canvas: Canvas) {
        canvas.save()
        canvas.clipRect(cropViewRect, Region.Op.DIFFERENCE)
        canvas.drawColor(mDimmedColor)
        canvas.restore()
    }

    /**
     * This method draws crop bounds (empty rectangle)
     * and crop guidelines (vertical and horizontal lines inside the crop bounds) if needed.
     *
     * @param canvas - valid canvas object
     */
    private fun drawCropGrid(canvas: Canvas) {
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
    }

    /**
     * This method draws divider bounds (empty rectangle)
     * and divider guidelines (vertical and horizontal lines inside the crop bounds) if needed.
     *
     * @param canvas - valid canvas object
     */
    private fun drawDividerGrid(canvas: Canvas) {
        if (mShowDividerGrid) {
            if (mDividerGridPoints == null && !dividerViewRect.isEmpty) {

                mDividerGridPoints = FloatArray(mDividerGridRowCount * 4 + mDividerGridColumnCount * 4)

                var index = 0
                for (i in 0 until mDividerGridRowCount) {
                    mDividerGridPoints!![index++] = dividerViewRect.left
                    mDividerGridPoints!![index++] = dividerViewRect.height() * ((i.toFloat() + 1.0f) / (mDividerGridRowCount + 1).toFloat()) + dividerViewRect.top
                    mDividerGridPoints!![index++] = dividerViewRect.right
                    mDividerGridPoints!![index++] = dividerViewRect.height() * ((i.toFloat() + 1.0f) / (mDividerGridRowCount + 1).toFloat()) + dividerViewRect.top
                }

                for (i in 0 until mDividerGridColumnCount) {
                    mDividerGridPoints!![index++] = dividerViewRect.width() * ((i.toFloat() + 1.0f) / (mDividerGridColumnCount + 1).toFloat()) + dividerViewRect.left
                    mDividerGridPoints!![index++] = dividerViewRect.top
                    mDividerGridPoints!![index++] = dividerViewRect.width() * ((i.toFloat() + 1.0f) / (mDividerGridColumnCount + 1).toFloat()) + dividerViewRect.left
                    mDividerGridPoints!![index++] = dividerViewRect.bottom
                }
            }

            if (mDividerGridPoints != null) {
                canvas.drawLines(mDividerGridPoints, mDividerGridPaint)

                when (mDividerType) {
                    DivideType.VERTICAL_CENTER_DIVIDE -> {
                        canvas.drawRect(0f, 0f, width / 3f, height.toFloat(), mDividerPaint)
                        canvas.drawRect(width * 2 / 3f, 0f, width.toFloat(), height.toFloat(), mDividerPaint)
                    }
                    DivideType.VERTICAL_LEFT_DIVIDE -> {
                        canvas.drawRect(width * 1 / 3f, 0f, width.toFloat(), height.toFloat(), mDividerPaint)
                    }
                    DivideType.VERTICAL_RIGHT_DIVIDE -> {
                        canvas.drawRect(0f, 0f, width * 2 / 3f, height.toFloat(), mDividerPaint)
                    }
                    DivideType.HORIZONTAL_CENTER_DIVIDE -> {
                        canvas.drawRect(0f, 0f, width.toFloat(), height / 3f, mDividerPaint)
                        canvas.drawRect(0f, height * 2 / 3f, width.toFloat(), height.toFloat(), mDividerPaint)
                    }
                    DivideType.HORIZONTAL_TOP_DIVIDE -> {
                        canvas.drawRect(0f, height * 1 / 3f, width.toFloat(), height.toFloat(), mDividerPaint)
                    }
                    DivideType.HORIZONTAL_BOT_DIVIDE -> {
                        canvas.drawRect(0f, 0f, width.toFloat(), height * 2 / 3f, mDividerPaint)
                    }
                }
            }
        }
    }

    /**
     * This method extracts all needed values from the styled attributes.
     * Those are used to configure the view.
     */
    fun processStyledAttributes(a: TypedArray) {
        mDimmedColor = a.getColor(R.styleable.RedactorView_dimmed_color,
                resources.getColor(R.color.color_default_dimmed))
        mDimmedStrokePaint.color = mDimmedColor
        mDimmedStrokePaint.style = Paint.Style.STROKE
        mDimmedStrokePaint.strokeWidth = 1f

        initCropGridStyle(a)
        initDividersGridStyle(a)
        initDividerRectStyle(a)
        mShowCropGrid = a.getBoolean(R.styleable.RedactorView_show_grid, DEFAULT_SHOW_CROP_GRID)
        mShowDividerGrid = a.getBoolean(R.styleable.RedactorView_show_divider_grid, DEFAULT_SHOW_DIVIDER_GRID)
    }


    /**
     * This method setups Paint object for the crop guidelines.
     */
    private fun initCropGridStyle(a: TypedArray) {
        val cropGridStrokeSize = a.getDimensionPixelSize(R.styleable.RedactorView_grid_stroke_size,
                resources.getDimensionPixelSize(R.dimen.default_crop_grid_stoke_width))
        val cropGridColor = a.getColor(R.styleable.RedactorView_grid_color,
                resources.getColor(R.color.color_default_crop_grid))
        mCropGridPaint.strokeWidth = cropGridStrokeSize.toFloat()
        mCropGridPaint.color = cropGridColor

        mCropGridRowCount = a.getInt(R.styleable.RedactorView_grid_row_count, DEFAULT_CROP_GRID_ROW_COUNT)
        mCropGridColumnCount = a.getInt(R.styleable.RedactorView_grid_column_count, DEFAULT_CROP_GRID_COLUMN_COUNT)
    }

    private fun initDividersGridStyle(a: TypedArray) {
        val dividerStrokeSize = a.getDimensionPixelSize(R.styleable.RedactorView_divider_stroke_size,
                resources.getDimensionPixelSize(R.dimen.default_divider_stoke_width))
        val dividerColor = a.getColor(R.styleable.RedactorView_divider_grid_color,
                resources.getColor(R.color.color_default_divider_grid))
        mDividerGridPaint.strokeWidth = dividerStrokeSize.toFloat()
        mDividerGridPaint.color = dividerColor

        mDividerGridRowCount = a.getInt(R.styleable.RedactorView_divider_grid_row_count, DEFAULT_DIVIDER_GRID_ROW_COUNT)
        mDividerGridColumnCount = a.getInt(R.styleable.RedactorView_grid_column_count, DEFAULT_DIVIDER_GRID_COLUMN_COUNT)
    }

    private fun initDividerRectStyle(a: TypedArray) {
        val dividerRectColor = a.getColor(R.styleable.RedactorView_divider_color,
                resources.getColor(R.color.color_default_divider))

        mDividerPaint.color = dividerRectColor
    }
}