package com.adrian.simplemvpframe.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.adrian.simplemvpframe.R

/**
 * date:2018/6/13
 * author：RanQing
 * description：
 */
class SingleColChartView : View {

    //坐标轴数据
    private lateinit var xLabelList: Array<String>
    private lateinit var yLabelList: Array<String>

    //数据
    private lateinit var dataList: IntArray
    //背景色
    private var background: Int = Color.WHITE
    //柱形通用颜色
    private var normalColor: Int = Color.BLACK
    //柱形触摸颜色
    private var touchColor: Int = Color.BLACK
    //显示X轴
    private var xAxesVisible: Boolean = true
    //显示Y轴
    private var yAxesVisible: Boolean = true
    //坐标轴颜色
    private var axesColor: Int = Color.BLACK
    //文字颜色
    private var txtColor: Int = Color.BLACK
    //文字尺寸
    private var txtSize: Float = 15f
    //数值显示类型(不显示/点击显示/总是显示)
    private var showValueType: Int = 0
    //默认边距
    private val margin: Int = 20
    //距离左边偏移量
    private val marginX: Int = 30
    //原点坐标
    private var xPoint = 0
    private var yPoint = 0
    //XY轴单位长度
    private var xScale: Int = 0
    private var yScale: Int = 0

    //柱形宽度
    private var colWidth: Float = 0f
    private var touchIndex = -1

    private lateinit var paintAxes: Paint
    private lateinit var paintAxesTxt: Paint
    private lateinit var paintRectF: Paint
    private lateinit var paintValue: Paint

    var listener: OnClickColumnListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val ta: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SingleColChartView, defStyleAttr, 0)
        normalColor = ta.getColor(R.styleable.SingleColChartView_normalColor, Color.BLACK)
        touchColor = ta.getColor(R.styleable.SingleColChartView_touchColor, Color.BLACK)
        background = ta.getColor(R.styleable.SingleColChartView_bgColor, Color.WHITE)
        xAxesVisible = ta.getBoolean(R.styleable.SingleColChartView_xAxesVisible, true)
        yAxesVisible = ta.getBoolean(R.styleable.SingleColChartView_yAxesVisible, true)
        axesColor = ta.getColor(R.styleable.SingleColChartView_axesColor, Color.BLACK)
        txtColor = ta.getColor(R.styleable.SingleColChartView_txtColor, Color.BLACK)
        txtSize = ta.getDimension(R.styleable.SingleColChartView_txtSize, 15f)
        showValueType = ta.getInt(R.styleable.SingleColChartView_showValueType, 0)
        ta.recycle()

        initData()
    }

    private fun initData() {

        paintAxes = Paint()
        paintAxes.isAntiAlias = true
        paintAxes.style = Paint.Style.STROKE
        paintAxes.strokeWidth = 4f
        paintAxes.isDither = true
        paintAxes.color = axesColor

        paintAxesTxt = Paint()
        paintAxesTxt.isAntiAlias = true
        paintAxesTxt.style = Paint.Style.STROKE
        paintAxesTxt.textSize = txtSize
        paintAxesTxt.isDither = true
        paintAxesTxt.color = txtColor

        paintRectF = Paint()
        paintRectF.isDither = true
        paintRectF.isAntiAlias = true
        paintRectF.style = Paint.Style.FILL
        paintRectF.strokeWidth = 1f
        paintRectF.color = normalColor

        paintValue = Paint()
        paintValue.isAntiAlias = true
        paintValue.isDither = true
        paintValue.style = Paint.Style.STROKE
        paintValue.textSize = txtSize
        paintValue.textAlign = Paint.Align.CENTER
        paintValue.color = txtColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val width = if (widthMode == MeasureSpec.EXACTLY) widthSize else widthSize.shr(1)
        val height = if (heightMode == MeasureSpec.EXACTLY) heightSize else heightSize.shr(1)

        setMeasuredDimension(width, height)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        xPoint = margin + marginX
        yPoint = height - margin
        xScale = (width - 2 * margin - marginX) / (xLabelList!!.size - 1)
        yScale = (height - 2 * margin) / (yLabelList!!.size - 1)
        colWidth = xScale / 2f

        logE("xScale:$xScale, yScale:$yScale")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        canvas?.drawColor(background)

        drawAxesLine(canvas!!)
        drawAxesTxt(canvas!!)
        drawColumn(canvas!!, dataList!!)
        drawValue(canvas!!, dataList!!)
    }

    /**
     * 绘制坐标轴
     */
    private fun drawAxesLine(canvas: Canvas) {
        //X
        if (xAxesVisible) {
            canvas.drawLine(xPoint.toFloat(), yPoint.toFloat(), width - margin / 6f, yPoint.toFloat(), paintAxes)
            canvas.drawLine(width - margin / 6f, yPoint.toFloat(), width - margin / 2f, yPoint - margin / 3f, paintAxes)
            canvas.drawLine(width - margin / 6f, yPoint.toFloat(), width - margin / 2f, yPoint + margin / 3f, paintAxes)
        }

        //Y
        if (yAxesVisible) {
            canvas.drawLine(xPoint.toFloat(), yPoint.toFloat(), xPoint.toFloat(), margin / 6f, paintAxes)
            canvas.drawLine(xPoint.toFloat(), margin / 6f, xPoint - margin / 3f, margin / 2f, paintAxes)
            canvas.drawLine(xPoint.toFloat(), margin / 6f, xPoint + margin / 3f, margin / 2f, paintAxes)
        }
    }

    /**
     * 绘制坐标文字
     */
    private fun drawAxesTxt(canvas: Canvas) {
        //X
        for ((index, value) in xLabelList!!.withIndex()) {
            paintAxesTxt.textAlign = Paint.Align.CENTER
            val startX = xPoint + index * xScale
            canvas.drawText(value, startX.toFloat(), height - margin / 6f, paintAxesTxt)
        }

        //Y
        for ((index, value) in yLabelList!!.withIndex()) {
            paintAxesTxt.textAlign = Paint.Align.LEFT
            val startY = yPoint - index * yScale
            val offsetX = when (yLabelList!!.size) {
                1 -> 28
                2 -> 20
                3 -> 12
                4 -> 5
                else -> 0
            }
            val offsetY = if (index == 0) 0 else margin / 5
            canvas.drawText(value, margin / 4f + offsetX, startY.toFloat() + offsetY, paintAxesTxt)
        }
    }

    /**
     * 绘制柱形
     */
    private fun drawColumn(canvas: Canvas, data: IntArray) {
        try {
            val halfW = colWidth / 2
            for ((index, value) in data.withIndex()) {
                val startX: Int = xPoint + (index + 1) * xScale
                val rect = RectF(startX - halfW, toY(value), startX + halfW, height - margin - 2f)
                paintRectF.color = if (showValueType == 1 && touchIndex == index) touchColor else normalColor
                canvas.drawRect(rect, paintRectF)
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("图表数据异常")
        }
    }

    /**
     * 绘制数值
     */
    private fun drawValue(canvas: Canvas, data: IntArray) {
        when (showValueType) {
            1 -> showClicked(canvas, data)
            2 -> showAlways(canvas, data)
            else -> showNever(canvas, data)
        }
    }

    /**
     * 点击显示数值
     */
    private fun showClicked(canvas: Canvas, data: IntArray) {
        if (touchIndex >= 0 && touchIndex < data.size) {
            canvas.drawText("${data[touchIndex]}", xPoint.toFloat() + (touchIndex+1) * xScale, toY(data[touchIndex]) - 5, paintValue)
        }
    }

    /**
     * 始终不显示数值
     */
    private fun showNever(canvas: Canvas, data: IntArray) {
        for (i in 1..(xLabelList!!.size - 1)) {
            canvas.drawText("", xPoint.toFloat() + i * xScale, toY(data[i - 1]) - 5, paintValue)
        }
    }

    /**
     * 一直显示数值
     */
    private fun showAlways(canvas: Canvas, data: IntArray) {
        for ((index, value) in data.withIndex()) {
            canvas.drawText("$value", xPoint.toFloat() + (index + 1) * xScale, toY(value) - 5, paintValue)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x
        val y = event!!.y
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchIndex = ((x - xPoint)/xScale).toInt()
//                logE("touchIndex:$touchIndex")
                if (showValueType == 1 && touchIndex >= 0 && touchIndex < dataList.size) {
                    listener?.clickColumn(touchIndex, dataList[touchIndex])
                }
                invalidate()
            }
        }
        return true
    }

    /**
     * 设置数据
     */
    fun setData(data: IntArray, xLabels: Array<String>, yLabels: Array<String>) {
        this.dataList = data
        this.xLabelList = xLabels
        this.yLabelList = yLabels

        if (data.size > (xLabelList.size - 1)) {
            throw IllegalArgumentException("数据长度大于横轴坐标长度")
            return
        }
        invalidate()
    }

    /**
     * 获取颜色值
     */
    private fun getColor(@ColorRes id: Int): Int {
        return ContextCompat.getColor(context, id)
    }

    private fun toY(num: Int): Float {
        var y: Float
        try {
            val a: Float = num / 100f
            y = yPoint - a * yScale
        } catch (e: Exception) {
            return 0f
        }
        return y
    }

    private fun logE(msg: String) {
        Log.e("CHARTVIEW", msg)
    }

    open interface OnClickColumnListener {
        fun clickColumn(index: Int, value: Int)
    }
}