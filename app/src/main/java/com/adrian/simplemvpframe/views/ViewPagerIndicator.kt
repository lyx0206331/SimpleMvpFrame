package com.adrian.simplemvpframe.views

import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import android.widget.LinearLayout
import com.adrian.simplemvpframe.R
import org.jetbrains.anko.backgroundColor

/**
 * date:2018/11/28 16:05
 * author：RanQing
 * description：
 */
class DotsIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    companion object {
        const val DEFAULT_POINT_COLOR = Color.CYAN
        const val DEFAULT_WIDTH_FACTOR = 2.5f
    }

    var dots: ArrayList<ImageView>? = null
    var viewPager: ViewPager? = null
    var dotsSize = 0f
    var dotsCornerRadius = 0f
    var dotsSpacing = 0f
    var currentPage = 0
    var dotsWidthFactor = DEFAULT_WIDTH_FACTOR
    @ColorInt
    var dotsColor = DEFAULT_POINT_COLOR
    var dotsClickable = false
    var pageChangedListener: ViewPager.OnPageChangeListener? = null

    init {
        dots = arrayListOf()
        orientation = HORIZONTAL

        dotsSize = dp2px(context, 16)
        dotsSpacing = dp2px(context, 4)
        dotsCornerRadius = dotsSize / 2

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DotsIndicator)
            dotsColor = a.getColor(R.styleable.DotsIndicator_dotsColor, DEFAULT_POINT_COLOR)
            setUpCircleColors(dotsColor)

            dotsWidthFactor = a.getFloat(R.styleable.DotsIndicator_dotsWidthFactor, 2.5f)
            if (dotsWidthFactor < 1) {
                dotsWidthFactor = 2.5f
            }
            dotsSize = a.getDimension(R.styleable.DotsIndicator_dotsSize, dotsSize)
            dotsCornerRadius = a.getDimension(R.styleable.DotsIndicator_dotsCornerRadius, dotsSize / 2)
            dotsSpacing = a.getDimension(R.styleable.DotsIndicator_dotsSpacing, dotsSpacing)

            a.recycle()
        } else {
            setUpCircleColors(DEFAULT_POINT_COLOR)
        }

        if (isInEditMode) {
            addDots(5)
        }
    }

    private fun setUpCircleColors(@ColorInt color: Int) {
        dots?.forEach {
            //            ((it.background) as GradientDrawable).color = color
            it.backgroundColor = color
        }
    }

    private fun addDots(count: Int) {
        for (i in 0 until count) {

        }
    }

}

fun dp2px(context: Context, dp: Int): Float {
    return context.resources.displayMetrics.density * dp
}

fun getThemePrimaryColor(context: Context): Int {
    val value = TypedValue()
    context.theme.resolveAttribute(R.attr.colorPrimary, value, true)
    return value.data
}