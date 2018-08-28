package com.adrian.circleprogressbarlib

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.IntDef
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

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

        const val STOP_ANIM_SIMPLE = 0
        const val STOP_ANIM_REVERSE = 1

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
    private val mProgressCenterPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var mRadius = 0f
    private var mCenterX = 0f
    private var mCenterY = 0f

    //是否显示进度值
    var mShowValue: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

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

    var mCenterColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            mProgressCenterPaint.color = value
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
    var mCenterDrawable: Drawable? = null
        set(value) {
            field = value
            invalidate()
        }
    //格式化进度值为特殊格式
    var mProgressFormatter: ProgressFormatter = DefaultProgressFormatter()
        set(value) {
            field = value
            invalidate()
        }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(STOP_ANIM_SIMPLE, STOP_ANIM_REVERSE)
    annotation class StopAnimType

    //停止动画类型
    @StopAnimType
    var mStopAnimType = 0

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

    var mOnPressedListener: OnPressedListener? = null

    private var mAnimator: ValueAnimator? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (context == null) return

        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyleAttr, 0)

        mLineCount = a.getInt(R.styleable.CircleProgressBar_line_count, DEFAULT_LINE_COUNT)
        mStopAnimType = a.getInt(R.styleable.CircleProgressBar_stop_anim_type, STOP_ANIM_SIMPLE)
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
        mCenterColor = a.getColor(R.styleable.CircleProgressBar_center_color, Color.TRANSPARENT)
        mShowValue = a.getBoolean(R.styleable.CircleProgressBar_show_value, true)
        mCenterDrawable = a.getDrawable(R.styleable.CircleProgressBar_center_src)

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

        mProgressCenterPaint.style = Paint.Style.FILL
        mProgressCenterPaint.color = mCenterColor
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
                    if (mRadius <= 0) return
                    shader = RadialGradient(mCenterX, mCenterY, mRadius, mProgressStartColor, mProgressEndColor, Shader.TileMode.CLAMP)
                }
                SWEEP -> {
                    if (mRadius <= 0) return
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

        drawCenterColor(canvas)

        drawCenterDrawable(canvas)

        drawProgressText(canvas)
    }

    /**
     * 绘制进度值
     */
    private fun drawProgressText(canvas: Canvas?) {
        if (mProgressFormatter == null || !mShowValue) return

        val progressText = mProgressFormatter.format(mProgress, mMax)

        if (TextUtils.isEmpty(progressText)) return

        mProgressTextPaint.textSize = mProgressTextSize
        mProgressTextPaint.color = mProgressTextColor
        mProgressTextPaint.getTextBounds(progressText.toString(), 0, progressText.length, mProgressTextRect)
        canvas?.drawText(progressText, 0, progressText.length, mCenterX, mCenterY + mProgressTextRect.height() / 2, mProgressTextPaint)
    }

    /**
     * 中间未填充颜色时，绘制居中颜色
     */
    private fun drawCenterColor(canvas: Canvas?) {
        if (mStyle == LINE || mStyle == SOLID_LINE) {
            canvas?.drawCircle(mCenterX, mCenterY, mRadius - mProgressStrokeWidth, mProgressCenterPaint)
        }
    }

    /**
     * 绘制居中图片
     */
    private fun drawCenterDrawable(canvas: Canvas?) {
        try {
            if ((mStyle == LINE || mStyle == SOLID_LINE) && mCenterDrawable != null) {
                val bmp: Bitmap = (mCenterDrawable as BitmapDrawable).bitmap
                val bmpWidth = bmp.width
                val bmpHeight = bmp.height
                val bmpRect = Rect(0, 0, bmpWidth, bmpHeight)
                val desLeft: Int = (mCenterX - bmpWidth / 2).toInt()
                val desTop: Int = (mCenterY - bmpHeight / 2).toInt()
                val desRight = desLeft + bmpWidth
                val desBottom = desTop + bmpHeight
                val desRect = Rect(desLeft, desTop, desRight, desBottom)
                canvas?.drawBitmap(bmp, bmpRect, desRect, mProgressCenterPaint)
            }
        } catch (e: Exception) {
            Utils.logE("CircleProgressBar", "Exception:${e.message}")
        }
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
     * 居中绘制表盘式线状圆环
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
     * 绘制实心扇形圆弧
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
     * 绘制实心线形圆弧
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

        //防止进度条被裁剪
        mProgressRectF.inset(mProgressStrokeWidth / 2, mProgressStrokeWidth / 2)
    }

    /**
     * 开始动画
     * @param duration 动画执行时长.默认1s
     * @param start 动画开始时进度.默认0
     * @param end 动画终止时进度.默认最大值
     */
    fun startAnimator(duration: Long = 1000, start: Int = 0, end: Int = mMax) {
        if (mAnimator == null) {
            mAnimator = ValueAnimator()
        }
        mAnimator?.setIntValues(if (start < 0) 0 else start, if (end > mMax) mMax else end)
        mAnimator?.addUpdateListener {
            mProgress = it.animatedValue as Int
            mOnPressedListener?.onPressProcess(mProgress)
            if (mProgress == end) mOnPressedListener?.onPressStop(mProgress)
        }
        mAnimator?.duration = duration
        mAnimator?.repeatCount = 0
        mAnimator?.start()

        mOnPressedListener?.onPressStart()
    }

    /**
     * 停止动画
     */
    fun stopAnimator() {
        if (mAnimator != null && mAnimator!!.isRunning) {
            mOnPressedListener?.onPressStop(mAnimator!!.animatedValue as Int)
            if (mStopAnimType == STOP_ANIM_SIMPLE) {    //直接停止动画并恢复到进度0
                mAnimator?.cancel()
                mProgress = 0
            } else {    //动画回退到进度0
                mAnimator?.reverse()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null || !isClickable) return true
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startAnimator(5000)
            }
            MotionEvent.ACTION_UP -> {
                stopAnimator()
            }
            MotionEvent.ACTION_CANCEL -> {
                stopAnimator()
            }
        }
        return super.onTouchEvent(event)
    }

    interface OnPressedListener {
        //按下时响应
        fun onPressStart()

        //按下过程中响应，带当前进度值
        fun onPressProcess(progress: Int)

        //结束按下响应，带结束时的进度值
        fun onPressStop(progress: Int)
    }

    interface ProgressFormatter {
        fun format(progress: Int, max: Int): CharSequence
    }

    class DefaultProgressFormatter : ProgressFormatter {
        private val defaultPattern = "%d%%"

        override fun format(progress: Int, max: Int): CharSequence {
            return java.lang.String.format(defaultPattern, (progress.toFloat() / max.toFloat() * 100).toInt())
        }

    }

    class SavedState : BaseSavedState {

        var progress = 0

        constructor(source: Parcelable) : super(source)
        constructor(source: Parcel) : super(source) {
            progress = source.readInt()
        }

        override fun writeToParcel(out: Parcel?, flags: Int) {
            super.writeToParcel(out, flags)
            out?.writeInt(progress)
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
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