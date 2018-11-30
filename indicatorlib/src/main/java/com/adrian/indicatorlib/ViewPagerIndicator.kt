package com.adrian.indicatorlib

import android.content.Context
import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.ColorInt
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

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

    val dots by lazy {
        arrayListOf<ImageView>()
    }
    var mViewPager: ViewPager? = null
        set(value) {
            field = value
            setUpViewPager()
            refreshDots()
        }
    var dotsSize = dp2px(context, 16)
    var dotsCornerRadius = dotsSize / 2
    var dotsSpacing = dp2px(context, 4)
    var currentPage = 0
    var dotsWidthFactor = DEFAULT_WIDTH_FACTOR
    @ColorInt
    var dotsColor = DEFAULT_POINT_COLOR
    var dotsClickable = true
    var pageChangedListener: ViewPager.OnPageChangeListener? = null

    init {
        orientation = HORIZONTAL

//        dotsSize = dp2px(context, 16)
//        dotsSpacing = dp2px(context, 4)
//        dotsCornerRadius = dotsSize / 2

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.DotsIndicator, defStyleAttr, 0)
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshDots()
    }

    private fun refreshDots() {
        if (mViewPager != null && mViewPager?.adapter != null) {
            if (dots?.size.orZero() < mViewPager?.adapter?.count.orZero()) {
                addDots(mViewPager?.adapter?.count.orZero() - dots?.size.orZero())
            } else if (dots?.size.orZero() > mViewPager?.adapter?.count.orZero()) {
                removeDots(dots?.size.orZero() - mViewPager?.adapter?.count.orZero())
            }
            setUpDotsAnimators()
        } else {
            Log.e(DotsIndicator::class.java.simpleName,
                    "You have to set an adapter to the view pager before !")
        }
    }

    private fun setUpDotsAnimators() {
        mViewPager?.apply {
            if (adapter != null && adapter?.count.orZero() > 0) {
                if (currentPage < dots?.size.orZero()) {
                    val dot = dots?.get(currentPage)
                    dot.apply {
                        val params = layoutParams as RelativeLayout.LayoutParams
                        params.width = dotsSize.toInt()
                        dot?.layoutParams = params
                    }
                }

                currentPage = currentItem.orZero()
                if (currentPage >= dots?.size.orZero()) {
                    currentPage = dots?.size.orZero() - 1
                    setCurrentItem(currentPage, false)
                }
                val dot = dots?.get(currentPage)
                dot.apply {
                    val params = layoutParams as RelativeLayout.LayoutParams
                    params.width = (dotsSize * dotsWidthFactor).toInt()
                    dot?.layoutParams = params
                }

                if (pageChangedListener != null) {
                    removeOnPageChangeListener(pageChangedListener!!)
                }
                setUpOnPageChangedListener()
                addOnPageChangeListener(pageChangedListener!!)
            }
        }
    }

    private fun setUpOnPageChangedListener() {
        pageChangedListener = object : ViewPager.OnPageChangeListener {

            var lastPage: Int = 0

            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (position != currentPage && positionOffset.toInt() == 0 || currentPage < position) {
                    setDotWidth(dots?.get(currentPage), dotsSize.toInt())
                    currentPage = position
                }

                if (abs(currentPage - position) > 1) {
                    setDotWidth(dots?.get(currentPage), dotsSize.toInt())
                    currentPage = lastPage
                }

                var dot = dots?.get(currentPage)

                var nextDot: ImageView? = null
                if (currentPage == position && currentPage + 1 < dots?.size.orZero()) {
                    nextDot = dots?.get(currentPage + 1)
                } else if (currentPage > position) {
                    nextDot = dot
                    dot = dots?.get(currentPage - 1)
                }

                val dotWidth = (dotsSize + (dotsSize * (dotsWidthFactor - 1) * (1 - positionOffset))).toInt()
                setDotWidth(dot, dotWidth)

                if (nextDot != null) {
                    val nextDotWidth = (dotsSize + (dotsSize * (dotsWidthFactor - 1) * positionOffset)).toInt()
                    setDotWidth(nextDot, nextDotWidth)
                }

                lastPage = position
            }

            private fun setDotWidth(dot: ImageView?, dotWidth: Int) {
                dot?.apply {
                    val dotParams = layoutParams
                    dotParams.width = dotWidth
                    layoutParams = dotParams
                }
            }

            override fun onPageSelected(p0: Int) {
            }
        }
    }

    private fun setUpViewPager() {
        mViewPager?.adapter?.apply {
            registerDataSetObserver(object : DataSetObserver() {
                override fun onChanged() {
                    super.onChanged()
                    refreshDots()
                }
            })
        }
    }

    private fun removeDots(count: Int) {
        for (i in 0 until count) {
            removeViewAt(childCount - 1)
            dots?.removeAt(dots?.size.orZero() - 1)
        }
    }

    private fun setUpCircleColors(@ColorInt color: Int) {
        dots?.forEach {
            ((it.background) as GradientDrawable).setColor(color)
        }
    }

    private fun addDots(count: Int) {
        for (i in 0 until count) {
            val dot = LayoutInflater.from(context).inflate(R.layout.dot_layout, this, false)
            val iv = dot.findViewById<ImageView>(R.id.dot)
            val lp: RelativeLayout.LayoutParams = iv.layoutParams as RelativeLayout.LayoutParams
            lp.width = dotsSize.toInt()
            lp.height = dotsSize.toInt()
            lp.setMargins(dotsSpacing.toInt(), 0, dotsSpacing.toInt(), 0)
            (iv.background as GradientDrawable).cornerRadius = dotsCornerRadius
            (iv.background as GradientDrawable).setColor(dotsColor)

            dot.setOnClickListener {
                if (dotsClickable && mViewPager != null && mViewPager?.adapter != null && i < mViewPager?.adapter?.count.orZero()) {
                    mViewPager?.setCurrentItem(i, true)
                }
            }

            dots?.add(iv)
            addView(dot)
        }
    }

    fun setPointsColor(@ColorInt color: Int) {
        setUpCircleColors(color)
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

fun Int?.orZero(): Int = this ?: 0

fun Boolean?.orFalse(): Boolean = this ?: false