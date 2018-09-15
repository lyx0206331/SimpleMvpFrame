package com.adrian.simplemvpframe.views

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.support.annotation.IntDef
import android.util.AttributeSet
import android.widget.TextView
import org.jetbrains.anko.textColor

/**
 * date:2018/9/15 14:55
 * author：RanQing
 * description：
 */
class FilletLabelTextView : TextView {

    companion object {

        const val STROKE = 0    //线性边框,实心填充
        const val SOLID = 1     //无边框，实心填充
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(STROKE, SOLID)
    annotation class BgType

    @BgType
    var mBgType = STROKE
        set(value) {
            field = value
            when (value) {
                STROKE -> {
                    mBorderPaint.color = mBorderColor
                    mSolidPaint.color = mSolidColor
                }
                SOLID -> {
                    mBorderPaint.color = mSolidColor
                }
            }
            invalidate()
        }

    private var mBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mSolidPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    //边框颜色
    var mBorderColor = Color.GREEN
        set(value) {
            field = value
            mBgType = mBgType
        }
    //边框宽度
    var mBorderLineWidth = 6f
        set(value) {
            field = value
            invalidate()
        }
    //实心填充颜色
    var mSolidColor = Color.WHITE
        set(value) {
            field = value
            mBgType = mBgType
        }

    //圆角半径
    var mFilletRadius = 16f
        set(value) {
            field = value
            invalidate()
        }

    private var mBorderWidth = 0f
    private var mBorderHeight = 0f
    private var mSolidWidth = 0f
    private var mSolidHeight = 0f

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mBorderPaint.isAntiAlias = true
        mBorderPaint.color = mBorderColor
        mBorderPaint.style = Paint.Style.FILL

        mSolidPaint.isAntiAlias = true
        mSolidPaint.color = mSolidColor
        mSolidPaint.style = Paint.Style.FILL

        mBgType = STROKE
    }

    override fun onDraw(canvas: Canvas?) {
        val innerRadius = mFilletRadius - mBorderLineWidth

        when (mBgType) {
            SOLID -> {
                canvas?.drawRoundRect(RectF(0f, 0f, mBorderWidth, mBorderHeight), mFilletRadius, mFilletRadius, mBorderPaint)
            }
            STROKE -> {
//                drawRoundRect(0f, 0f, mBorderWidth, mBorderHeight, -1, mFilletRadius, canvas, mBorderPaint)
//                drawRoundRect(mBorderLineWidth, mBorderLineWidth, mBorderWidth - mBorderLineWidth, mBorderHeight - mBorderLineWidth, -1, innerRadius, canvas, mSolidPaint)
                canvas?.drawRoundRect(RectF(0f, 0f, mBorderWidth, mBorderHeight), mFilletRadius, mFilletRadius, mBorderPaint)
                canvas?.drawRoundRect(RectF(mBorderLineWidth, mBorderLineWidth, mSolidWidth, mSolidHeight), innerRadius, innerRadius, mSolidPaint)

            }
        }

        super.onDraw(canvas)
    }

    /**
     * 绘制圆角矩形
     *
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param type
     * @param canvas
     */
    private fun drawRoundRect(left: Float, top: Float, right: Float, bottom: Float, type: Int, radius: Float, canvas: Canvas?, paint: Paint) {
        val path = Path()
        path.addRoundRect(RectF(left, top, right, bottom), getRoundRectRadius(type, radius), Path.Direction.CW)
        canvas?.drawPath(path, paint)
    }

    /**
     * 获取圆角半径数组
     *
     * @param type   圆角类型
     * @param radius 圆角半径
     * @return
     */
    private fun getRoundRectRadius(type: Int, radius: Float): FloatArray {
        return when (type) {
            0 //左上左下圆角
            -> floatArrayOf(radius, radius, 0f, 0f, 0f, 0f, radius, radius)
            1 //全直角
            -> floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
            2 //右上右下圆角
            -> floatArrayOf(0f, 0f, radius, radius, radius, radius, 0f, 0f)
            else    //全圆角
            -> floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mBorderWidth = w.toFloat()
        mBorderHeight = h.toFloat()
        mSolidWidth = w - mBorderLineWidth
        mSolidHeight = h - mBorderLineWidth
    }
}