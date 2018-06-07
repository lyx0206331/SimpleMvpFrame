package com.adrian.simplemvpframe.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.adrian.simplemvpframe.R

/**
 * date:2018/6/6
 * author：RanQing
 * description：
 */
class ChartView : View {

    //双柱左侧
    var leftColor: Int = Color.BLACK
    //双柱右侧
    var rightColor: Int = Color.BLACK
    //横轴线
    var lineColor: Int = Color.BLACK
    //点击选中左侧
    var selectLeftColor: Int = Color.BLACK
    //点击选中右侧
    var selectRightColor: Int = Color.BLACK
    //左侧底部
    var leftColorBottom: Int = Color.BLACK
    //右侧底部
    var rightColorBottom: Int = Color.BLACK
    //横轴画笔
    private lateinit var mPaint: Paint
    //柱状图画笔
    private lateinit var mChartPaint: Paint
    //阴影画笔
    private lateinit var mShadowPaint: Paint
    //屏幕宽度
    private var mWidth: Int = 0
    //屏幕高度
    private var mHeight: Int = 0
    //柱状图起始位置
    private var mStartWidth: Int = 0
    //柱状图宽度
    private var mChartWidth: Int = 0
    private var mSize: Int = 0
    private lateinit var mBound: Rect
    //柱状图高度占比
    var list: List<Float> = ArrayList()
        set(value) {
            field = value
            mSize = width / 39
            mStartWidth = width / 13
            mChartWidth = width / 13 - mSize - 3
            invalidate()
        }
    //柱状图矩形
    private lateinit var rect: Rect
    //点击接口
    lateinit var listener: OnClickNumberListener
    //柱状图最大值
    private var maxNumber: Int = 1000
    //点击选中柱状图索引
    private var selectIndex: Int = -1
    private var selectIndexRoles: ArrayList<Int> = ArrayList()

    open interface OnClickNumberListener {
        fun clickNumber(number: Int, x: Int, y: Int)
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val array: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ChartView, defStyleAttr, 0)
        leftColor = array.getColor(R.styleable.ChartView_leftColor, Color.BLACK)
        rightColor = array.getColor(R.styleable.ChartView_rightColor, Color.BLACK)
        selectLeftColor = array.getColor(R.styleable.ChartView_selectLeftColor, Color.BLACK)
        selectRightColor = array.getColor(R.styleable.ChartView_selectRightColor, Color.BLACK)
        lineColor = array.getColor(R.styleable.ChartView_xyColor, Color.BLACK)
        leftColorBottom = array.getColor(R.styleable.ChartView_leftColorBottom, Color.BLACK)
        rightColorBottom = array.getColor(R.styleable.ChartView_rightColorBottom, Color.BLACK)
        array.recycle()

        initPainter()
    }

    private fun initPainter() {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mBound = Rect()
        mChartPaint = Paint()
        mChartPaint.isAntiAlias = true
        mShadowPaint = Paint()
        mShadowPaint.isAntiAlias = true
        mShadowPaint.color = Color.WHITE
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

        mWidth = width
        mHeight = height
        mStartWidth = width / 13
        mSize = width / 39
        mChartWidth = width / 13 - mSize
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        mPaint.color = lineColor
        //坐标轴
//        canvas!!.drawLine(0f, mHeight - 100f, mWidth.toFloat(), mHeight - 100f, mPaint)
        for (i in 1..12) {
            //画刻度线
            canvas!!.drawLine(mStartWidth.toFloat(), mHeight - 100f, mStartWidth.toFloat(), mHeight - 80f, mPaint)
            //画数字
            mPaint.textSize = 35f
            mPaint.textAlign = Paint.Align.CENTER
            mPaint.getTextBounds(i.toString(), 0, i.toString().length, mBound)
            canvas.drawText("${i}月", mStartWidth - mBound.width().shr(1).toFloat(), mHeight - 60f + mBound.height().shr(1), mPaint)
            mStartWidth += width / 13
        }

        //画柱状图
        for ((index, value) in list.withIndex()) {
            val size = mHeight / 120;
            if (selectIndexRoles.contains(index)) {
                //偶数
                mChartPaint.shader = null
                mChartPaint.color = if (index % 2 == 0) selectLeftColor else selectRightColor
            } else {
                if (index % 2 == 0) {
                    val lg = LinearGradient(mChartWidth.toFloat(), mChartWidth.toFloat() + mSize, mHeight - 100f,
                            mHeight - 100 - value * mSize, leftColorBottom, leftColor, Shader.TileMode.MIRROR)
                    mChartPaint.shader = lg
                } else {
                    val lg = LinearGradient(mChartWidth.toFloat(), mChartWidth.toFloat() + mSize, mHeight - 100f,
                            mHeight - 100 - value * mSize, rightColorBottom, rightColor, Shader.TileMode.MIRROR)
                    mChartPaint.shader = lg
                }
            }

            mChartPaint.style = Paint.Style.FILL
            //画阴影
            val rectF: RectF = RectF()
            rectF.left = mChartWidth.toFloat()
            rectF.right = mChartWidth.toFloat() + mSize
            rectF.bottom = mHeight - 100f
            rectF.top = mHeight - 100f - value * size
//            canvas!!.drawRoundRect(rectF, 10f, 10f, mChartPaint)
            //长方形
            canvas!!.drawRect(mChartWidth.toFloat(), mHeight - 100f - value * size, mChartWidth.toFloat() + mSize, mHeight - 100f, mChartPaint)
            mChartWidth += if (index % 2 == 0) (3 + width) / 39 else (width / 13 - 3 - mSize)
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        if (visibility == VISIBLE) {
            mSize = width / 39
            mStartWidth = width / 13
            mChartWidth = width / 13 - mSize - 3
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event!!.x.toInt()
        val y = event!!.y.toInt()
        var left: Int = 0
        var right: Int = mWidth / 12
        var top: Int = 0
        var bottom: Int = mHeight - 100
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                for (i in 0..11) {
                    rect = Rect(left, top, right, bottom)
                    left += mWidth / 12
                    right += mWidth / 12
                    if (rect.contains(x, y)) {
                        listener?.clickNumber(i, x, y)
                        maxNumber = i
                        selectIndex = i
                        selectIndexRoles.clear()
                        selectIndexRoles.add(selectIndex.shl(1) + 1)
                        selectIndexRoles.add(selectIndex.shl(1))
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }
}