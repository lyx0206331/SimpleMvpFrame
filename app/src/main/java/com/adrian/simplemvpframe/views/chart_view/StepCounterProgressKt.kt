package com.adrian.simplemvpframe.views.chart_view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.animation.Interpolator
import android.widget.RelativeLayout
import com.adrian.simplemvpframe.R

/**
 * Created by Administrator on 2018/6/4.
 */
class StepCounterProgressKt : RelativeLayout {

//    companion object {
//        val TAG: String = StepCounterProgressKt::class.simpleName!!
//    }

    var interpolator: Interpolator? = null
        set(value) {
            field = value
            if (valueAnimator != null) {
                valueAnimator?.interpolator = interpolator
            }
        }
    var basePaintColor: Int = Color.YELLOW
    var progressColor: Int = Color.WHITE
    var min: Float = 0f
    var begin: Float = 0f
    var max: Float = 100f
    var valueAnimator: ValueAnimator? = null
    var onValueChangeListener: OnValueChangeListener? = null
    var value: Float = 0f
        set(value) {
            if (field >= max) {
                log("超出最大值")
                return
            }
            if (isContinue) {
                field += value
            } else {
                begin = min
                field = value
            }

            field = if (field > max) max else field
            field = if (field < min) min else field

            isComplete = false
            invalidate()
            if (value <= max || value >= min) {
                animateValue()
            }
        }
    var duration: Int = 1000

    var progressStrokeWidth: Int = 48
    private var paddingT: Int = 0
    private var isComplete: Boolean = false

    var dashWidth = 5
    var dashSpace = 8

    private lateinit var progressPaint: Paint
    private var outPadding: Int = 0
    private lateinit var basePaint: Paint
    private lateinit var baseCircle: RectF
    private lateinit var progressCircle: RectF
    private var baseStartAngle = 270f
    private var baseFinishAngle = 360f
    private var progressStartAngle = 270f
    private var plusAngle = 0f
    private var paddingR = 0
    private var paddingL = 0
    private var paddingB = 0
    private var isContinue = false

    val log = { msg: String -> Log.e("StepCounterProgressKt", msg) }

    interface OnValueChangeListener {
        fun onValueChange(value: Float)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        setWillNotDraw(false)
        val attributes: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.StepCounterProgress, defStyleAttr, 0)

        paddingB = paddingBottom + 15
        paddingL = paddingLeft + 15
        paddingR = paddingRight + 15
        paddingT = paddingTop + 15

        paddingT += progressStrokeWidth.shr(1)
        paddingB += progressStrokeWidth.shr(1)
        paddingL += progressStrokeWidth.shr(1)
        paddingR += progressStrokeWidth.shr(1)

        initAttributes(attributes)
    }

    private fun initAttributes(attributes: TypedArray) {
        basePaintColor = attributes.getColor(R.styleable.StepCounterProgress_base_color, basePaintColor)
        progressColor = attributes.getColor(R.styleable.StepCounterProgress_progress_color, progressColor)
        max = attributes.getFloat(R.styleable.StepCounterProgress_max, max)
        duration = attributes.getInt(R.styleable.StepCounterProgress_duration, duration)
        progressStrokeWidth = attributes.getDimensionPixelOffset(R.styleable.StepCounterProgress_progress_stroke_width, progressStrokeWidth)

        attributes.recycle()

        initBasePainter()
        initProgressPainter()
        initValueAnimator()
    }

    private fun initBasePainter() {
        basePaint = Paint()
        basePaint.isAntiAlias = true
        basePaint.strokeWidth = progressStrokeWidth.toFloat()
        basePaint.color = basePaintColor
        basePaint.style = Paint.Style.STROKE
        basePaint.pathEffect = DashPathEffect(floatArrayOf(dashWidth.toFloat(), dashSpace.toFloat()), dashSpace.toFloat())
    }

    private fun initProgressPainter() {
        progressPaint = Paint()
        progressPaint.isAntiAlias = true
        progressPaint.strokeWidth = progressStrokeWidth.toFloat()
        progressPaint.color = progressColor
        progressPaint.style = Paint.Style.STROKE
        progressPaint.pathEffect = DashPathEffect(floatArrayOf(dashWidth.toFloat(), dashSpace.toFloat()), dashSpace.toFloat())
    }

    private fun initValueAnimator() {
        valueAnimator = ValueAnimator()
        valueAnimator!!.interpolator = interpolator
        valueAnimator!!.addUpdateListener(ValueAnimatorListenerImp())
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        log("onLayout")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        log("onMeasure")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        log("onSizeChanged")
        initBaseRectF(h, w)
        initProgressRectF(h, w)
    }

    private fun initBaseRectF(h: Int, w: Int) {
        baseCircle = RectF()
        baseCircle.set(paddingL.toFloat(), paddingT.toFloat(), (w - paddingR).toFloat(), (h - paddingB).toFloat())
    }

    private fun initProgressRectF(h: Int, w: Int) {
        progressCircle = RectF()
        progressCircle.set(paddingL.toFloat(), paddingT.toFloat(), (w - paddingR).toFloat(), (h - paddingB).toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        log("onDraw,baseAngle:$baseFinishAngle -- plusAngle:$plusAngle")
        canvas?.drawArc(baseCircle, baseStartAngle, baseFinishAngle, false, basePaint)
        canvas?.drawArc(progressCircle, progressStartAngle, plusAngle, false, progressPaint)

        if (isComplete) {
            return
        }
        invalidate()
    }

    /**
     * 执行属性动画
     */
    private fun animateValue() {
        if (valueAnimator != null) {
            valueAnimator?.setFloatValues(begin, value)
            valueAnimator?.setDuration(duration.toLong())
            valueAnimator?.start()
        }
    }

    /**
     * 开启连续模式，每次在原有的进度上增加进度
     */
    fun beginContinue(b: Boolean) {
        isContinue = b
    }

    fun reset() = {
        begin = min

//        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator?.cancel()
    }

    inner class ValueAnimatorListenerImp : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator?) {
            val value: Float = animation?.getAnimatedValue() as Float

            plusAngle = (baseFinishAngle * value) / max
            if (onValueChangeListener != null) {
                onValueChangeListener?.onValueChange(value)
            }

            begin = value

            if (begin == value) {
                isComplete = true
            }
        }

    }
}