package com.adrian.simplemvpframe.views

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.RelativeLayout
import com.adrian.simplemvpframe.R

/**
 * Created by Administrator on 2018/6/4.
 */
class DHealthyProgressViewKt @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : RelativeLayout(context, attrs, defStyleAttr) {


    private var interpolator: Interpolator = AccelerateDecelerateInterpolator()

    private var dBaserPainterColor = Color.YELLOW
    var progressColor = Color.WHITE
    var min = 0f
    private var begin = min
    var max = 100f
    private var valueAnimator: ValueAnimator? = null
    private var valueChangeListener: OnValueChangeListener? = null
    private var mValue: Float = 0.toFloat()
    var duration = 1000

    private var progressStrokeWidth = 48
    private var paddingT: Int = 0
    private var isComplete: Boolean = false

    private val dashWith = 5
    private val dashSpace = 8


    private var progressPaint: Paint? = null
    private val outPadding: Int = 0
    private var basePaint: Paint? = null
    private var baseCircle: RectF? = null
    private var progressCircle: RectF? = null
    private val baseStartAngle = 270f
    private val baseFinishAngle = 359.8f
    private val progressStartAngle = 270f
    private var plusAngle: Float = 0.toFloat()
    private var paddingR: Int = 0
    private var paddingL: Int = 0
    private var paddingB: Int = 0
    private var isContinue: Boolean = false

    init {
        init(context, attrs, defStyleAttr)
    }


    private fun init(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) {
        setWillNotDraw(false)
        val attributes = context.obtainStyledAttributes(attributeSet, R.styleable.StepCounterProgress, defStyleAttr, 0)
        //获取padding属性

        paddingT = paddingTop
        paddingL = paddingLeft
        paddingR = paddingRight
        paddingB = paddingBottom


        paddingT = paddingT + progressStrokeWidth / 2
        paddingL = paddingL + progressStrokeWidth / 2
        paddingR = paddingR + progressStrokeWidth / 2
        paddingB = paddingB + progressStrokeWidth / 2

        Log.e(TAG, "padding " + paddingT)

        initAttributes(attributes)


    }

    private fun initAttributes(attributes: TypedArray) {

        dBaserPainterColor = attributes.getColor(R.styleable.StepCounterProgress_base_color,
                dBaserPainterColor)
        progressColor = attributes.getColor(R.styleable.StepCounterProgress_progress_color,
                progressColor)
        max = attributes.getFloat(R.styleable.StepCounterProgress_max, max)
        duration = attributes.getInt(R.styleable.StepCounterProgress_duration, duration)

        progressStrokeWidth = attributes.getDimensionPixelOffset(R.styleable.StepCounterProgress_progress_stroke_width,
                progressStrokeWidth)
        initPainters()


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e(TAG, "onMeasure")
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        Log.e(TAG, "onlayout")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        Log.e(TAG, "initRectF $w<--->$h")

        initBaseRectF(h, w)
        initProgressRectF(h, w)

    }

    private fun initProgressRectF(h: Int, w: Int) {
        progressCircle = RectF()
        progressCircle!!.set(paddingL.toFloat(), paddingT.toFloat(), (w - paddingR).toFloat(), (h - paddingB).toFloat())
    }

    private fun initBaseRectF(h: Int, w: Int) {
        baseCircle = RectF()
        baseCircle!!.set(paddingL.toFloat(), paddingT.toFloat(), (w - paddingR).toFloat(), (h - paddingB).toFloat())
    }

    private fun initPainters() {


        initBasePainter()

        initProgressPainter()

        initValueAnimator()


    }

    /**
     * 初始化进度画笔
     */
    private fun initProgressPainter() {
        progressPaint = Paint()
        progressPaint!!.isAntiAlias = true
        progressPaint!!.strokeWidth = progressStrokeWidth.toFloat()
        progressPaint!!.color = progressColor
        progressPaint!!.style = Paint.Style.STROKE
        progressPaint!!.pathEffect = DashPathEffect(floatArrayOf(dashWith.toFloat(), dashSpace.toFloat()),
                dashSpace.toFloat())

    }

    /**
     * 初始化 底层 画笔
     */
    private fun initBasePainter() {
        basePaint = Paint()
        basePaint!!.isAntiAlias = true
        basePaint!!.strokeWidth = progressStrokeWidth.toFloat()
        basePaint!!.color = dBaserPainterColor
        basePaint!!.style = Paint.Style.STROKE
        basePaint!!.pathEffect = DashPathEffect(floatArrayOf(dashWith.toFloat(), dashSpace.toFloat()),
                dashSpace.toFloat())

    }


    /**
     * 进度显示动画效果初始化
     */
    private fun initValueAnimator() {
        valueAnimator = ValueAnimator()
        valueAnimator!!.interpolator = interpolator
        valueAnimator!!.addUpdateListener(ValueAnimatorListenerImp())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        Log.e(TAG, "onDraw")


        canvas.drawArc(baseCircle!!, baseStartAngle, baseFinishAngle, false, basePaint!!)

        canvas.drawArc(progressCircle!!, progressStartAngle, plusAngle, false, progressPaint!!)

        if (isComplete) {
            return
        }
        invalidate()

    }

    /**
     * 设置进度值 重新开始绘制

     * @param mValue
     */
    fun setmValue(mValue: Float) {

        if (this.mValue >= max) {
            Log.e(TAG, "最大值超出")
            return
        }

        if (isContinue) {
            this.mValue += mValue
        } else {
            begin = min
            this.mValue = mValue
        }


        //最大值 最小值的限制
        this.mValue = if (this.mValue > max) max else this.mValue

        this.mValue = if (this.mValue < min) min else this.mValue

        isComplete = false
        invalidate()
        if (mValue <= max || mValue >= min) {
            animateValue()
        }
    }

    /**
     * 属性动画开始
     */
    private fun animateValue() {
        if (valueAnimator != null) {
            valueAnimator!!.setFloatValues(begin, mValue)
            valueAnimator!!.duration = duration.toLong()
            valueAnimator!!.start()

        }
    }

    fun setOnValueChangeListener(valueChangeListener: OnValueChangeListener) {
        this.valueChangeListener = valueChangeListener
    }

    fun setInterpolator(interpolator: Interpolator) {
        this.interpolator = interpolator
        if (valueAnimator != null) {
            valueAnimator!!.interpolator = interpolator
        }
    }

    /**
     * 开启连续模式 每次在原有的进度上增加进度
     * @param b
     */
    fun beginContinue(b: Boolean) {
        isContinue = b
    }

    /**
     * 动画开始 更新进度显示
     */
    private inner class ValueAnimatorListenerImp : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
            val value = valueAnimator.animatedValue as Float

            plusAngle = baseFinishAngle * value / max

            if (valueChangeListener != null) {
                valueChangeListener!!.onValueChange(value)
            }

            begin = value

            if (begin == mValue) {
                isComplete = true
            }
        }


    }

    interface OnValueChangeListener {
        fun onValueChange(value: Float)
    }

    fun reset() {
        begin = min
    }

    fun getdBaserPainterColor(): Int {
        return dBaserPainterColor
    }

    fun setdBaserPainterColor(dBaserPainterColor: Int) {
        this.dBaserPainterColor = dBaserPainterColor
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator!!.cancel()
    }

    companion object {
        private val TAG = DHealthyProgressView::class.java.name
    }
}