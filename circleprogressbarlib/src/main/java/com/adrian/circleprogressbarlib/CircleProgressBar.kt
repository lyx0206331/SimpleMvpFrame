package com.adrian.circleprogressbarlib

import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IntDef
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import java.lang.annotation.RetentionPolicy

/**
 * date:2018/8/27 11:43
 * author：RanQing
 * description：
 */
class CircleProgressBar : View {

    companion object {
        const val DEFAULT_MAX = 100
        const val MAX_DEGREE = 360f
        const val LINEAR_START_DEGREE = 90f

        const val LINE = 0
        const val SOLID = 1
        const val SOLID_LINE = 2

        const val LINEAR = 0
        const val RADIAL = 1
        const val SWEEP = 2

        const val DEFAULT_START_DEGREE = -90f
        const val DEFAULT_LINE_COUNT = 45
        const val DEFAULT_LINE_WIDTH = 4f
        const val DEFAULT_PROGRESS_TEXT_SIZE = 11f
        const val DEFAULT_PROGRESS_STROKE_WIDTH = 1f

        const val COLOR_FFF2A670 = "#fff2a670"
        const val COLOR_FFD3D3D5 = "#ffe3e3e5"
    }

    private val mProgressRectF = RectF()
    private val mProgressTextRect = Rect()

    private val mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mProgressBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mProgressTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mRadius = 0f
    private var mCenterX = 0f
    private var mCenterY = 0f

    var mProgress = 0
        set(value) {
            field = value
            invalidate()
        }
    private var mMax = DEFAULT_MAX
        set(value) {
            field = value
            invalidate()
        }

    //Only work well in the Line Style,represents the line count of the rings included
    var mLineCount = 0
        set(value) {
            field = value
            invalidate()
        }
    //Only work well in the Line Style,Height of the line of the progress bar
    var mLineWidth = 0f
        set(value) {
            field = value
            invalidate()
        }

    //进度条宽度
    var mProgressStrokeWidth = 0f
        set(value) {
            field = value
            mProgressRectF.inset(value / 2, value / 2)
            invalidate()
        }
    //进度条文字大小
    var mProgressTextSize = 0f
        set(value) {
            field = value
            invalidate()
        }
    //进度条进度开始颜色
    var mProgressStartColor = Color.BLACK
        set(value) {
            field = value
            updateProgressShader()
            invalidate()
        }
    //进度条进度结束颜色
    var mProgressEndColor = Color.BLACK
        set(value) {
            field = value
            updateProgressShader()
            invalidate()
        }
    //进度条文字颜色
    var mProgressTextColor = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }
    //进度条背景色
    var mProgressBackgroundColor = Color.WHITE
        set(value) {
            field = value
            mProgressBackgroundPaint.color = value
            invalidate()
        }

    //进度条旋转起始角度.默认-90
    var mStartDegree = -90f
        set(value) {
            field = value
            invalidate()
        }
    //是否只在进度条之外绘制背景色
    var mDrawBackgroundOutsideProgress = false
        set(value) {
            field = value
            invalidate()
        }
    //格式化进度值为特殊格式
    var mProgressFormatter = DefaultProgressFormatter()
        set(value) {
            field = value
            invalidate()
        }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LINE, SOLID, SOLID_LINE)
    annotation class Style

    //进度条颜色样式
    @Style
    var mStyle = LINE
        set(value) {
            field = value
            mProgressPaint.style = if (value == SOLID) Paint.Style.FILL else Paint.Style.STROKE
            mProgressBackgroundPaint.style = if (value == SOLID) Paint.Style.FILL else Paint.Style.STROKE
            invalidate()
        }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LINEAR, RADIAL, SWEEP)
    annotation class ShaderMode

    //画笔形状
    @ShaderMode
    var mShader = LINEAR
        set(value) {
            field = value
            updateProgressShader()
            invalidate()
        }
    //The Stroke cap of mProgressPaint and mProgressBackgroundPaint
    var mCap: Paint.Cap = Paint.Cap.BUTT
        set(value) {
            field = value
            mProgressPaint.strokeCap = value
            mProgressBackgroundPaint.strokeCap = value
            invalidate()
        }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (context == null) return

        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyleAttr, 0)

        mLineCount = a.getInt(R.styleable.CircleProgressBar_line_count, DEFAULT_LINE_COUNT)
        mStyle = a.getInt(R.styleable.CircleProgressBar_style, LINE)
        mShader = a.getInt(R.styleable.CircleProgressBar_progress_shader, LINEAR)
        mCap = if (a.hasValue(R.styleable.CircleProgressBar_progress_stroke_cap)) Paint.Cap.values()[a.getInt(R.styleable.CircleProgressBar_progress_stroke_cap, 0)] else Paint.Cap.BUTT
        mLineWidth = a.getDimension(R.styleable.CircleProgressBar_line_width, Utils.dip2px(context, DEFAULT_LINE_WIDTH))
        mProgressTextSize = a.getDimension(R.styleable.CircleProgressBar_progress_text_size, DEFAULT_PROGRESS_TEXT_SIZE)
        mProgressStrokeWidth = a.getDimension(R.styleable.CircleProgressBar_progress_stroke_width, DEFAULT_PROGRESS_STROKE_WIDTH)
        mProgressStartColor = a.getColor(R.styleable.CircleProgressBar_progress_start_color, Color.parseColor(COLOR_FFF2A670))
        mProgressEndColor = a.getColor(R.styleable.CircleProgressBar_progress_end_color, Color.parseColor(COLOR_FFF2A670))
        mProgressTextColor = a.getColor(R.styleable.CircleProgressBar_progress_text_color, Color.parseColor(COLOR_FFF2A670))
        mProgressBackgroundColor = a.getColor(R.styleable.CircleProgressBar_progress_background_color, Color.parseColor(COLOR_FFD3D3D5))
        mStartDegree = a.getFloat(R.styleable.CircleProgressBar_progress_start_degree, DEFAULT_START_DEGREE)
        mDrawBackgroundOutsideProgress = a.getBoolean(R.styleable.CircleProgressBar_drawBackgroundOutsideProgress, false)

        a.recycle()

        initPaint()
    }

    private fun initPaint() {
        mProgressTextPaint.textAlign = Paint.Align.CENTER
        mProgressTextPaint.textSize = mProgressTextSize

        mProgressPaint.style = if (mStyle == SOLID) Paint.Style.FILL else Paint.Style.STROKE
        mProgressPaint.strokeWidth = mProgressStrokeWidth
        mProgressPaint.color = mProgressStartColor
        mProgressPaint.strokeCap = mCap

        mProgressBackgroundPaint.style = if (mStyle == SOLID) Paint.Style.FILL else Paint.Style.STROKE
        mProgressBackgroundPaint.strokeWidth = mProgressStrokeWidth
        mProgressBackgroundPaint.color = mProgressBackgroundColor
        mProgressBackgroundPaint.strokeCap = mCap
    }

    /**
     * 渐变进度条
     * 需要在onSizeChanged中执行{@link #onSizeChanged(int, int, int, int)}
     */
    private fun updateProgressShader() {
        if (mProgressStartColor != mProgressEndColor) {
            var shader: Shader? = null
            when (mShader) {
                LINEAR -> {
                    shader = LinearGradient(mProgressRectF.left, mProgressRectF.top, mProgressRectF.left, mProgressRectF.bottom, mProgressStartColor, mProgressEndColor, Shader.TileMode.CLAMP)
                    val matrix = Matrix()
                    matrix.setRotate(LINEAR_START_DEGREE, mCenterX, mCenterY)
                    shader.getLocalMatrix(matrix)
                }
                RADIAL -> {
                    shader = RadialGradient(mCenterX, mCenterY, mRadius, mProgressStartColor, mProgressEndColor, Shader.TileMode.CLAMP)
                }
                SWEEP -> {
                    val radian = mProgressStrokeWidth / Math.PI * 2f / mRadius
                    val rotateDegrees: Float = -(if (mCap == Paint.Cap.BUTT && mStyle == SOLID_LINE) 0f else Math.toDegrees(radian).toFloat())
                    shader = SweepGradient(mCenterX, mCenterY, intArrayOf(mProgressStartColor, mProgressEndColor), floatArrayOf(0f, 1f))
                    val matrix = Matrix()
                    matrix.setRotate(rotateDegrees, mCenterX, mCenterY)
                    shader.setLocalMatrix(matrix)
                }
            }
            mProgressPaint.shader = shader
        } else {
            mProgressPaint.shader = null
            mProgressPaint.color = mProgressStartColor
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.save()
        canvas?.rotate(mStartDegree, mCenterX, mCenterY)
        drawProgress(canvas)
        canvas?.restore()

        drawProgressText(canvas)
    }

    /**
     * 绘制进度值
     */
    private fun drawProgressText(canvas: Canvas?) {
        if (mProgressFormatter == null) return

        val progressText = mProgressFormatter.format(mProgress, mMax)

        if (TextUtils.isEmpty(progressText)) return

        mProgressTextPaint.textSize = mProgressTextSize
        mProgressTextPaint.color = mProgressTextColor
        mProgressTextPaint.getTextBounds(progressText.toString(), 0, progressText.length, mProgressTextRect)
        canvas?.drawText(progressText, 0, progressText.length, mCenterX, mCenterY + mProgressTextRect.height() / 2, mProgressTextPaint)
    }

    /**
     * 绘制进度样式
     */
    private fun drawProgress(canvas: Canvas?) {
        when (mStyle) {
            SOLID -> drawSolidProgress(canvas)
            SOLID_LINE -> drawSolidLineProgress(canvas)
            else -> drawLineProgress(canvas)
        }
    }

    /**
     * 居中绘制线状圆环
     */
    private fun drawLineProgress(canvas: Canvas?) {
        val unitDegrees: Float = (2f * Math.PI / mLineCount).toFloat()
        val outerCircleRadius = mRadius
        val interCircleRadius = mRadius - mLineWidth

        val progressLineCount = mProgress.toFloat() / mMax * mLineCount

        for (i in 0 until mLineCount) {
            val rotateDegrees = i * -unitDegrees

            val startX: Float = (mCenterX + Math.cos(rotateDegrees.toDouble()) * interCircleRadius).toFloat()
            val startY: Float = (mCenterY - Math.sin(rotateDegrees.toDouble()) * interCircleRadius).toFloat()

            val stopX: Float = (mCenterX + Math.cos(rotateDegrees.toDouble()) * outerCircleRadius).toFloat()
            val stopY: Float = (mCenterY - Math.sin(rotateDegrees.toDouble()) * outerCircleRadius).toFloat()

            if (mDrawBackgroundOutsideProgress) {
                if (i >= progressLineCount) canvas?.drawLine(startX, startY, stopX, stopY, mProgressBackgroundPaint)
            } else {
                canvas?.drawLine(startX, startY, stopX, stopY, mProgressBackgroundPaint)
            }

            if (i < progressLineCount) canvas?.drawLine(startX, startY, stopX, stopY, mProgressPaint)
        }
    }

    /**
     * 画圆弧
     */
    private fun drawSolidProgress(canvas: Canvas?) {
        if (mDrawBackgroundOutsideProgress) {
            val startAngle: Float = MAX_DEGREE * mProgress / mMax
            val sweepAngle: Float = MAX_DEGREE - startAngle
            canvas?.drawArc(mProgressRectF, startAngle, sweepAngle, true, mProgressBackgroundPaint)
        } else {
            canvas?.drawArc(mProgressRectF, 0f, MAX_DEGREE, true, mProgressBackgroundPaint)
        }
        canvas?.drawArc(mProgressRectF, 0f, MAX_DEGREE * mProgress / mMax, true, mProgressPaint)
    }

    /**
     * 绘制圆弧
     */
    private fun drawSolidLineProgress(canvas: Canvas?) {
        if (mDrawBackgroundOutsideProgress) {
            val startAngle: Float = MAX_DEGREE * mProgress / mMax
            val sweepAngle: Float = MAX_DEGREE - startAngle
            canvas?.drawArc(mProgressRectF, startAngle, sweepAngle, false, mProgressBackgroundPaint)
        } else {
            canvas?.drawArc(mProgressRectF, 0f, MAX_DEGREE, false, mProgressBackgroundPaint)
        }
        canvas?.drawArc(mProgressRectF, 0f, MAX_DEGREE * mProgress / mMax, false, mProgressPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2f
        mCenterY = h / 2f

        mRadius = Math.min(mCenterX, mCenterY)
        mProgressRectF.top = mCenterY - mRadius
        mProgressRectF.bottom = mCenterY + mRadius
        mProgressRectF.left = mCenterX - mRadius
        mProgressRectF.right = mCenterX + mRadius

        updateProgressShader()

        //Prevent the progress from clipping
        mProgressRectF.inset(mProgressStrokeWidth / 2, mProgressStrokeWidth / 2)
    }

    interface ProgressFormatter {
        fun format(progress: Int, max: Int): CharSequence
    }

    class DefaultProgressFormatter : ProgressFormatter {
        private val defaultPattern = "%d%%"

        override fun format(progress: Int, max: Int): CharSequence {
            return java.lang.String.format(defaultPattern, progress.toFloat() / max.toFloat() * 100)
        }

    }

    class SavedState : BaseSavedState {

        var progress = 0

        constructor(source: Parcelable) : super(source)
        constructor(source: Parcel?) : super(source) {
            if (source != null) {
                progress = source.readInt()
            }
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(progress)
        }

        val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
            override fun createFromParcel(p0: Parcel?): SavedState {
                return SavedState(p0)
            }

            override fun newArray(p0: Int): Array<SavedState?> {
                return arrayOfNulls<SavedState>(p0)
            }

        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        //强制保存祖先类状态
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)

        ss.progress = mProgress

        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val ss: SavedState = state as SavedState
        super.onRestoreInstanceState(ss.superState)

        mProgress = ss.progress
    }

}