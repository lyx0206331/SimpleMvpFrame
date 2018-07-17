package com.adrian.chartviewlib

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * date:2018/6/13
 * author：RanQing
 * description：
 */
class SingleColChartView : View {

    companion object {
        const val DATA_LENGTH_EXCEPTION = "数据长度大于横轴坐标长度"
        const val CHART_DATA_EXCEPTION = "图表数据异常"
        const val DATA_TOO_LARGE = "图表数据值太大"
    }

    //坐标轴数据
    private var xLabelList: Array<String>? = null
    private var yLabelList: Array<String>? = null

    //数据
    private var dataList: ArrayList<Int>? = null
    //背景色
    var background: Int = Color.WHITE
        set(value) {
            field = value
            invalidate()
        }
    //柱形通用颜色
    var normalColor: Int = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }
    //柱形触摸颜色
    var touchColor: Int = Color.BLACK
        set(value) {
            field = value
            invalidate()
        }
    //显示X轴
    var xAxesVisible: Boolean = true
        set(value) {
            field = value
            invalidate()
        }
    //显示Y轴
    var yAxesVisible: Boolean = true
        set(value) {
            field = value
            invalidate()
        }
    //虚线刻度是否可见
    var dashedVisible = false
        set(value) {
            field = value
            invalidate()
        }
    //虚线刻度类型(0线状虚线/1点状虚线/2点状渐变虚线)
    var dashedType = 0
        set(value) {
            field = value
            invalidate()
        }
    var targetLineIndex = 0
        set(value) {
            field = value
            invalidate()
        }
    //坐标轴颜色
    var axesColor: Int = Color.BLACK
        set(value) {
            field = value
            paintAxes.color = value
            invalidate()
        }
    //坐标轴是否带箭头
    var hasAxesArrow: Boolean = false
        set(value) {
            field = value
            invalidate()
        }
    //文字颜色
    var txtColor: Int = Color.BLACK
        set(value) {
            field = value
            paintAxesTxt.color = value
            paintValue.color = value
            invalidate()
        }
    //文字尺寸
    var txtSize: Float = 15f
        set(value) {
            field = value
            paintAxesTxt.textSize = value
            paintValue.textSize = value
            invalidate()
        }
    //数值显示类型(0不显示/1点击显示/2总是显示/3显示最大值/4显示最小值)
    var showValueType: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var showValueWay: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    //默认边距
    private val margin: Int = 40
    //距离左边偏移量
    private val marginX: Int = 30
    //原点坐标
    private var xPoint = 0
    private var yPoint = 0
    //XY轴单位长度
    private var xUnit: Int = 0
    private var yUnit: Int = 0
    var yUnitValue = 0f
        set(value) {
            field = if (value == 0f) 2500f else value
            invalidate()
        }

    //柱形宽度
    private var colWidth: Float = 0f
    private var touchIndex = -1

    private var paintAxes: Paint = Paint()
    private var paintAxesTxt: Paint = Paint()
    private var paintRectF: Paint = Paint()
    private var paintValue: Paint = Paint()
    private var paintDashed = Paint()

    var listener: OnClickColumnListener? = null
    var formater: ITextFormater? = null

    private var isClicked = false

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
        showValueWay = ta.getInt(R.styleable.SingleColChartView_showValueWay, 0)
        yUnitValue = ta.getFloat(R.styleable.SingleColChartView_yUnitValue, 100f)
        dashedVisible = ta.getBoolean(R.styleable.SingleColChartView_dashedVisible, false)
        dashedType = ta.getInt(R.styleable.SingleColChartView_dashedType, 0)
        hasAxesArrow = ta.getBoolean(R.styleable.SingleColChartView_hasAxesArrow, false)
        targetLineIndex = ta.getInt(R.styleable.SingleColChartView_tartgetLineIndex, 0)
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
        paintValue.textAlign = Paint.Align.CENTER
        paintValue.textSize = txtSize
        paintValue.color = txtColor

        paintDashed = Paint()
        paintDashed.isAntiAlias = true
        paintDashed.style = Paint.Style.STROKE
        paintDashed.strokeWidth = 3f
        paintDashed.isDither = true
        paintDashed.color = axesColor
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

        if (isAvailable()) {
            xPoint = margin + marginX
            yPoint = height - margin
            xUnit = (width - 2 * margin - marginX) / (xLabelList!!.size)
            yUnit = (height - 2 * margin) / (yLabelList!!.size - 1)
            colWidth = xUnit / 2f
        }
    }

    /**
     * 是否可绘制。轴坐标必须存在,y轴可以不显示
     */
    private fun isAvailable(): Boolean {
        if (xLabelList == null || xLabelList!!.isEmpty() || yLabelList == null || yLabelList!!.size < 2) {
            return false
        }
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null || !isAvailable()) return

        canvas?.drawColor(background)

        drawAxesLine(canvas!!)
        drawAxesTxt(canvas!!)
        drawColumn(canvas!!, dataList)
        drawValue(canvas!!, dataList)
    }

    /**
     * 绘制坐标轴
     */
    private fun drawAxesLine(canvas: Canvas) {
        //X
        if (xAxesVisible) {
            canvas.drawLine(xPoint.toFloat(), yPoint.toFloat(), width - margin / 6f, yPoint.toFloat(), paintAxes)
            //箭头
            if (hasAxesArrow) {
                canvas.drawLine(width - margin / 6f, yPoint.toFloat(), width - margin / 2f, yPoint - margin / 3f, paintAxes)
                canvas.drawLine(width - margin / 6f, yPoint.toFloat(), width - margin / 2f, yPoint + margin / 3f, paintAxes)
            }
        }

        //Y
        if (yAxesVisible) {
            canvas.drawLine(xPoint.toFloat(), yPoint.toFloat(), xPoint.toFloat(), margin / 6f, paintAxes)
            //箭头
            if (hasAxesArrow) {
                canvas.drawLine(xPoint.toFloat(), margin / 6f, xPoint - margin / 3f, margin / 2f, paintAxes)
                canvas.drawLine(xPoint.toFloat(), margin / 6f, xPoint + margin / 3f, margin / 2f, paintAxes)
            }
        }
    }

    /**
     * 绘制坐标文字
     */
    private fun drawAxesTxt(canvas: Canvas) {
        //X
        for ((index, value) in xLabelList!!.withIndex()) {
            paintAxesTxt.textAlign = Paint.Align.CENTER
            val startX = xPoint + (index + .5f) * xUnit
            canvas.drawText(value, startX, height - margin / 6f, paintAxesTxt)
        }

        //Y
        for ((index, value) in yLabelList!!.withIndex()) {
            paintAxesTxt.textAlign = Paint.Align.LEFT
            val startY = yPoint - index * yUnit
            val offsetY = if (index == 0) 0 else margin / 3
            canvas.drawText(value, 0f, startY.toFloat() + offsetY, paintAxesTxt)

            //绘制虚线
            if (!TextUtils.isEmpty(value?.trim())) {
                drawDashed(index, startY, canvas)
                if (targetLineIndex > 0 && index == targetLineIndex) {
                    var targetTxt = "目标"
                    if (yUnitValue == 2500f) {
                        targetTxt = "默认目标"
                    }
                    canvas.drawText(targetTxt, 0f, startY.toFloat() - offsetY.shl(1), paintAxesTxt)
                }
            }
        }
    }

    /**
     * 绘制虚线
     */
    private fun drawDashed(index: Int, startY: Int, canvas: Canvas) {
        if (dashedVisible && index > 0) {
            when (dashedType) {
                0 -> drawDashLine(startY, canvas)
                1 -> drawDashDot(startY, canvas)
                2 -> drawGradientDashDot(startY, canvas)
            }

        }
    }

    /**
     * 绘制线状虚线
     */
    private fun drawDashLine(startY: Int, canvas: Canvas) {
        paintDashed.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        val path = Path()
        path.moveTo(xPoint.toFloat(), startY.toFloat())
        path.lineTo(width - margin / 6f, startY.toFloat())
        canvas.drawPath(path, paintDashed)
    }

    /**
     * 绘制点状虚线
     */
    private fun drawDashDot(startY: Int, canvas: Canvas) {
        val path = Path()
        path.addCircle(0f, 0f, 3f, Path.Direction.CW)
        paintDashed.pathEffect = PathDashPathEffect(path, 15f, 0f, PathDashPathEffect.Style.ROTATE)
        path.moveTo(xPoint.toFloat(), startY.toFloat())
        path.lineTo(width - margin / 6f, startY.toFloat())
        canvas.drawPath(path, paintDashed)
    }

    /**
     * 绘制渐变点状虚线
     */
    private fun drawGradientDashDot(startY: Int, canvas: Canvas) {
        val path = Path()
        path.addCircle(0f, 0f, 3f, Path.Direction.CW)
        paintDashed.pathEffect = PathDashPathEffect(path, 15f, 0f, PathDashPathEffect.Style.ROTATE)
        paintDashed.shader = LinearGradient(0f, 0f, width - margin / 6f, 0f,
                intArrayOf(Color.TRANSPARENT, axesColor, Color.TRANSPARENT), floatArrayOf(0f, .5f, 1f), Shader.TileMode.CLAMP)
        path.moveTo(xPoint.toFloat(), startY.toFloat())
        path.lineTo(width - margin / 6f, startY.toFloat())
        canvas.drawPath(path, paintDashed)
    }

    /**
     * 绘制柱形
     */
    private fun drawColumn(canvas: Canvas, data: ArrayList<Int>?) {
        if (data == null || data.isEmpty()) {
            return
        }
        try {
            val halfW = colWidth / 2
            for ((index, value) in data.withIndex()) {
                val startX: Float = xPoint + (index + .5f) * xUnit
                val rect = RectF(startX - halfW, toY(value), startX + halfW, height - margin - 2f)
                paintRectF.color = if (showValueWay == 1 && touchIndex == index && isClicked) touchColor else normalColor
                canvas.drawRect(rect, paintRectF)
            }
        } catch (e: Exception) {
            throw IllegalArgumentException(CHART_DATA_EXCEPTION)
        }
    }

    /**
     * 绘制数值
     */
    private fun drawValue(canvas: Canvas, data: ArrayList<Int>?) {
        if (data == null || data.isEmpty()) {
            return
        }
        when (showValueType) {
            1 -> showMin(canvas, data)
            2 -> showMax(canvas, data)
        }
        when (showValueWay) {
            1 -> showClicked(canvas, data)
            2 -> showAlways(canvas, data)
        }
    }

    /**
     * 点击显示数值
     */
    private fun showClicked(canvas: Canvas, data: ArrayList<Int>) {
        if (touchIndex >= 0 && touchIndex < data.size && data[touchIndex] > 0) {
            if (isClicked) {
                val txt = "${data[touchIndex]}"
                if (formater != null) {
                    canvas.drawText(formater?.formatClickedTxt(txt, touchIndex), xPoint + (touchIndex + .5f) * xUnit, toY(data[touchIndex]) - margin, paintValue)
                }
                canvas.drawText(txt, xPoint + (touchIndex + .5f) * xUnit, toY(data[touchIndex]) - 5, paintValue)
            } /*else {
                canvas.drawText("", xPoint + (touchIndex + .5f) * xUnit, toY(data[touchIndex]) - 5, paintValue)
            }*/
        }
    }

    /**
     * 始终不显示数值
     */
    private fun showNever(canvas: Canvas, data: ArrayList<Int>) {
        for ((index, value) in data.withIndex()) {
            canvas.drawText("", xPoint + (index + .5f) * xUnit, toY(value) - 5, paintValue)
        }
    }

    /**
     * 一直显示数值
     */
    private fun showAlways(canvas: Canvas, data: ArrayList<Int>) {
        for ((index, value) in data.withIndex()) {
            if (value > 0) {
                canvas.drawText("$value", xPoint + (index + .5f) * xUnit, toY(value) - 5, paintValue)
            }
        }
    }

    /**
     * 显示最大值
     */
    private fun showMax(canvas: Canvas, data: ArrayList<Int>) {
        var maxIndex = 0
        for (index in data.indices) {
            if (data[maxIndex] < data[index]) {
                maxIndex = index
            }
        }
        canvas.drawText("${data[maxIndex]}", xPoint + (maxIndex + .5f) * xUnit, toY(data[maxIndex]) - 5, paintValue)
    }

    /**
     * 显示最小值
     */
    private fun showMin(canvas: Canvas, data: ArrayList<Int>) {
        var minIndex = 0
        for (index in data.indices) {
            if (data[minIndex] > data[index]) {
                minIndex = index
            }
        }
        canvas.drawText("${data[minIndex]}", xPoint + (minIndex + .5f) * xUnit, toY(data[minIndex]) - 5, paintValue)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (dataList == null || dataList!!.isEmpty() || event == null) {
            return true
        }
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchIndex = ((x - xPoint) / xUnit).toInt()
//                logE("touchIndex:$touchIndex")
                if (showValueWay == 1 && touchIndex >= 0 && touchIndex < dataList!!.size) {
                    isClicked = true
                    listener?.clickColumn(touchIndex, dataList!![touchIndex])
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                isClicked = false
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                isClicked = false
                invalidate()
            }
        }
        return true
    }

    /**
     * 设置数据
     */
    fun setData(@Nullable data: ArrayList<Int>?, @NotNull xLabels: Array<String>, @NotNull yLabels: Array<String>) {
        this.dataList = data
        this.xLabelList = xLabels
        this.yLabelList = yLabels

        requestLayout()

        if (data == null) {
            return
        }
        if (data.size > xLabelList!!.size) {
            showToast(DATA_LENGTH_EXCEPTION)
            logE(DATA_LENGTH_EXCEPTION)
//            throw IllegalArgumentException("数据长度大于横轴坐标长度")
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
            val a: Float = num / yUnitValue
            y = yPoint - a * yUnit
        } catch (e: Exception) {
            return 0f
        }
        return y
    }

    private fun logE(msg: String) {
        Log.e("CHARTVIEW", msg)
    }

    private fun showToast(@NotNull msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    open interface OnClickColumnListener {
        fun clickColumn(index: Int, value: Int)
    }

    interface ITextFormater {
        fun formatClickedTxt(txt: String, index: Int): String
    }

}