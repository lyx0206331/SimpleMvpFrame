package com.adrian.simplemvpframe.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.view.View
import com.adrian.simplemvpframe.R

/**
 * date:2018/6/8
 * author：RanQing
 * description：
 */
class CustomChartView : View {

    //坐标轴数据
    var xLabel: Array<String>? = null
    var yLabel: Array<String>? = null

    //数据
    var dataList: List<IntArray>? = null
    var colorList: List<Int>? = null
    //默认边距
    val margin: Int = 20
    //距离左边偏移量
    val marginX: Int = 30
    //原点坐标
    var xPoint = 0
    var yPoint = 0
    //XY轴单位长度
    var xScale: Int = 0
    var yScale: Int = 0

    private lateinit var paintAxes: Paint
    private lateinit var paintCoordinate: Paint
    private lateinit var paintRectF: Paint
    private lateinit var paintValue: Paint

    constructor(context: Context) : super(context)

    constructor(context: Context, xLabel: Array<String>, yLabel: Array<String>, dataList: List<IntArray>, colorList: List<Int>) : this(context) {
        this.xLabel = xLabel
        this.yLabel = yLabel
        this.dataList = dataList
        this.colorList = colorList

        initData()
    }

    private fun initData() {
        xPoint = margin + marginX
        yPoint = height - margin
        xScale = (width - 2 * margin - marginX) / (xLabel!!.size - 1)
        yScale = (height - 2 * margin) / (yLabel!!.size - 1)

        paintAxes = Paint()
        paintAxes.isAntiAlias = true
        paintAxes.style = Paint.Style.STROKE
        paintAxes.strokeWidth = 4f
        paintAxes.isDither = true
        paintAxes.color = getColor(R.color.leftColor)

        paintCoordinate = Paint()
        paintCoordinate.isAntiAlias = true
        paintCoordinate.style = Paint.Style.STROKE
        paintCoordinate.textSize = 15f
        paintCoordinate.isDither = true
        paintCoordinate.color = getColor(R.color.leftColor)

        paintRectF = Paint()
        paintRectF.isDither = true
        paintRectF.isAntiAlias = true
        paintRectF.style = Paint.Style.FILL
        paintRectF.strokeWidth = 1f

        paintValue = Paint()
        paintValue.isAntiAlias = true
        paintValue.isDither = true
        paintValue.style = Paint.Style.STROKE
        paintValue.textSize = 10f
        paintValue.textAlign = Paint.Align.CENTER
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawColor(getColor(R.color.rightColor))
    }

    /**
     * 绘制坐标轴
     */
    private fun drawAxesLine(canvas: Canvas, paint: Paint) {
        //X
        canvas.drawLine(xPoint.toFloat(), yPoint.toFloat(), width - margin / 6f, yPoint.toFloat(), paint)
        canvas.drawLine(width - margin / 6f, yPoint.toFloat(), width - margin / 2f, yPoint - margin / 3f, paint)
        canvas.drawLine(width - margin / 6f, yPoint.toFloat(), width - margin / 2f, yPoint + margin / 3f, paint)

        //Y
        canvas.drawLine(xPoint.toFloat(), yPoint.toFloat(), xPoint.toFloat(), margin / 6f, paint)
        canvas.drawLine(xPoint.toFloat(), margin / 6f, xPoint - margin / 3f, margin / 2f, paint)
        canvas.drawLine(xPoint.toFloat(), margin / 6f, xPoint + margin / 3f, margin / 2f, paint)
    }

    /**
     * 绘制刻度
     */
    private fun drawCoordinate(canvas: Canvas, paint: Paint) {
        //X
        for (i in 0..(xLabel!!.size - 1)) {
            paint.textAlign = Paint.Align.CENTER
            val startX: Int = xPoint + i * xScale
            canvas.drawText(xLabel!![i], startX.toFloat(), height - margin / 6f, paint)
        }

        //Y
        for (i in 0..(yLabel!!.size - 1)) {
            paint.textAlign = Paint.Align.LEFT
            val startY: Int = yPoint - i * yScale
            val offsetX: Int = when (yLabel!!.size) {
                1 -> 28
                2 -> 20
                3 -> 12
                4 -> 5
                else -> 0
            }
            val offsetY: Int = if (i == 0) 0 else margin / 5
            canvas.drawText(yLabel!![i], margin / 4f + offsetX, startY.toFloat() + offsetY, paint)
        }
    }

    /**
     * 绘制单柱形
     */
    private fun drawSingleBar(canvas: Canvas, paint: Paint, data: IntArray, colorList: List<Int>) {
        for (i in 1..(xLabel!!.size - 1)) {
            val startX: Int = xPoint + i * xScale
            val rect: RectF = RectF(startX - 5f, toY(data[i - 1]), startX + 5f, height - margin - 2f)
            val color: Int = getColor(colorList.get((i + 1) % 2))
            paint.color = color
            canvas.drawRect(rect, paint)
        }
    }

    /**
     * 绘制双柱形
     */
    private fun drawDoubleBars(canvas: Canvas, paint: Paint, dataList: List<IntArray>, colorList: List<Int>) {
        for (i in 1..(xLabel!!.size - 1)) {
            val startX: Int = xPoint + i * xScale
            paint.color = getColor(colorList[0])
            val rect1 = RectF(startX - 20f, toY(dataList.get(0)[i - 1]), startX - 10f, height - margin - 2f)
            canvas.drawRect(rect1, paint)

            paint.color = getColor(colorList[1])
            val rect2 = RectF(startX - 5f, toY(dataList.get(1)[i - 1]), startX + 5f, height - margin - 2f)
            canvas.drawRect(rect2, paint)
        }
    }

    /**
     * 绘制单数值
     */
    private fun drawSingleValue(canvas: Canvas, paint: Paint, data: IntArray, color: Int) {
        paint.color = getColor(color)
        for (i in 1..(xLabel!!.size - 1)) {
            canvas.drawText("${data[i - 1]}w", xPoint.toFloat() + i * xScale, toY(data[i - 1]) - 5, paintValue)
        }
//        for (i in xLabel!!.indices) {
//            canvas.drawText("${data[i]}w", xPoint.toFloat()+(i-1)*xScale, toY(data[i])-5, paintValue)
//        }
    }

    /**
     * 绘制双数组
     */
    private fun drawDoubleValues(canvas: Canvas, paint: Paint, dataList: List<IntArray>, color: Int) {
        paint.color = getColor(color)
        for (i in 1..(xLabel!!.size - 1)) {
            val startX = xPoint + i * xScale
            var offsetY1 = 5
            var offsetY2 = 5
            if (dataList[0][i - 1] == dataList[1][i - 1]) {
                offsetY2 += 10
            }
            if (i > 1) {
                if (dataList[1][i - 2] == dataList[0][i - 1]) {
                    offsetY1 += 10
                }
            }
            canvas.drawText("${dataList[0][i - 1]}w", startX - 18f, toY(dataList[0][i - 1]) - offsetY1, paintValue)
            canvas.drawText("${dataList[1][i - 1]}w", startX + 3f, toY(dataList[1][i - 1]) - offsetY2, paintValue)
        }
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
}