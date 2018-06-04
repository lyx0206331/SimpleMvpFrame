package com.adrian.simplemvpframe.views

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.widget.RelativeLayout
import com.adrian.simplemvpframe.R

/**
 * Created by Administrator on 2018/6/4.
 */
class StepCounterProgressKt : RelativeLayout {

    companion object {
        public val TAG: String = StepCounterProgressKt::class.simpleName!!
    }

    private val interpolator: Interpolator = AccelerateInterpolator()
    private var basePaintColor: Int = Color.YELLOW
    private var progressColor: Int = Color.WHITE
    private var min: Float = 0f
    private var begin: Float = 0f
    private var max: Float = 100f
    private lateinit var valueAnimator: ValueAnimator
    private lateinit var valueChangeListener: OnValueChangeListener
    private var value: Float = 0f
    private var duration: Int = 1000

    private var progressStrokeWidth: Int = 48
    private var paddingT: Int = 0
    private var isComplete: Boolean = false

    private var dashWidth = 5
    private var dashSpace = 8

    private lateinit var progressPaint: Paint
    private var outPadding: Int = 0
    private lateinit var basePaint: Paint
    private lateinit var baseCircle: RectF
    private lateinit var progressCircle: RectF
    private var baseStartAngle = 270f
    private var baseFinishAngle = 359.8f
    private var progressStartAngle = 270f
    private var plusAngle = 0f
    private var paddingR = 0
    private var paddingL = 0
    private var paddingB = 0
    private var isContinue = false

    interface OnValueChangeListener {
        fun onValueChange(value: Float)
    }

    constructor(context: Context) : super(context)

//    constructor(context: Context, attrs: AttributeSet) :super(context, attrs, 0) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        setWillNotDraw(false)
        val attributes: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.StepCounterProgress, defStyleAttr, 0)

        paddingB = paddingBottom
        paddingL = paddingLeft
        paddingR = paddingRight
        paddingT = paddingTop

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
        valueAnimator.interpolator = interpolator
        valueAnimator.addUpdateListener(ValueAnimatorListenerImp())
    }

    inner class ValueAnimatorListenerImp : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator?) {
            val value: Float = animation?.getAnimatedValue() as Float

            this@StepCounterProgressKt.plusAngle = (baseFinishAngle * value) / max
            if (valueChangeListener != null) {
                valueChangeListener.onValueChange(value)
            }

            begin = value

            if (begin == value) {
                isComplete = true
            }
        }

    }
}