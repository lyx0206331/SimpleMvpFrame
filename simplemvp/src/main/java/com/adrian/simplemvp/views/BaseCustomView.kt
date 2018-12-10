package com.adrian.simplemvp.views

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.min

/**
 * date:2018/12/10 9:44
 * author：RanQing
 * description：自定义控件基类
 */
class BaseCustomView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var widthSize = MeasureSpec.getSize(widthMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)
        var widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val width = if (widthMode == MeasureSpec.EXACTLY) widthSize else widthSize.shr(1)
        val height = if (heightMode == MeasureSpec.EXACTLY) heightSize else {
            val h = parseSize(100f).toInt()
            if (heightMode == MeasureSpec.AT_MOST) {
                min(heightSize, h)
            } else {
                h
            }
        }

        setMeasuredDimension(width, height)
    }

    /**
     * 格式化为两位数
     */
    protected fun formatValue(value: Int): String {
        return String.format("%02d", value)
    }

    /**
     * 根据单位转换尺寸
     */
    protected fun parseSize(size: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, size, resources.displayMetrics)
    }

    /**
     * 获取文字边界
     */
    protected fun getTextBounds(txt: String, paint: Paint): Rect {
        val rect = Rect()
        paint.getTextBounds(txt, 0, txt.length, rect)
        return rect
    }

    /**
     * 计算绘制文字时的基线到中轴线的距离
     */
    protected fun getBaseLine(paint: Paint): Float {
        val metrics = paint.fontMetrics
        return (metrics.descent - metrics.ascent) / 2 - metrics.descent
    }

    /**
     * 获取颜色值
     */
    protected fun getColor(@ColorRes resId: Int): Int {
        return ContextCompat.getColor(context, resId)
    }
}