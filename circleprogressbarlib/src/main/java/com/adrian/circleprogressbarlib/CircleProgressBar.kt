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

        const val DEFAULT_START_DEGREE = -90
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

    private var mProgress = 0
    private var mMax = DEFAULT_MAX

    //Only work well in the Line Style,represents the line count of the rings included
    private var mLineCount = 0
    //Only work well in the Line Style,Height of the line of the progress bar
    private var mLineWidth = 0f

    //进度条宽度
    private var mProgressStrokeWidth = 0f
    //进度条文字大小
    private var mProgressTextSize = 0f
    //进度条进度开始颜色
    private var mProgressStartColor = Color.BLACK
    //进度条进度结束颜色
    private var mProgressEndColor = Color.BLACK
    //进度条文字颜色
    private var mProgressTextColor = Color.BLACK
    //进度条背景色
    private var mProgressBackgroundColor = Color.WHITE

    //进度条旋转起始角度.默认-90
    private var mStartDegree = 0
    //是否只在进度条之外绘制背景色
    private var mDrawBackgroundOutsideProgress = false
    //格式化进度值为特殊格式
    private var mProgressFormatter = DefaultProgressFormatter()

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LINE, SOLID, SOLID_LINE)
    annotation class Style

    //进度条颜色样式
    @Style
    var mStyle = LINE

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(LINEAR, RADIAL, SWEEP)
    annotation class ShaderMode

    //画笔形状
    @ShaderMode
    var mShader = LINEAR
    //The Stroke cap of mProgressPaint and mProgressBackgroundPaint
    private var mCap: Paint.Cap = Paint.Cap.ROUND

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
        mStartDegree = a.getInt(R.styleable.CircleProgressBar_progress_start_degree, DEFAULT_START_DEGREE)
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

    /**
     * 绘制进度值
     */
    private fun drawProgressText(canvas: Canvas) {
        if (mProgressFormatter == null) return

        val progressText = mProgressFormatter.format(mProgress, mMax)

        if (TextUtils.isEmpty(progressText)) return

        mProgressTextPaint.textSize = mProgressTextSize
        mProgressTextPaint.color = mProgressTextColor
        mProgressTextPaint.getTextBounds(progressText.toString(), 0, progressText.length, mProgressTextRect)
        canvas.drawText(progressText, 0, progressText.length, mCenterX, mCenterY + mProgressTextRect.height() / 2, mProgressTextPaint)
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