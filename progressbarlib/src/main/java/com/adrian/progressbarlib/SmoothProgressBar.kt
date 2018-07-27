package com.adrian.progressbarlib

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar

/**
 * date:2018/7/27
 * author：RanQing
 * description：
 */
class SmoothProgressBar : ProgressBar {

    companion object {
        const val INTERPOLATOR_ACCELERATE = 0
        const val INTERPOLATOR_LINEAR = 1
        const val INTERPOLATOR_ACCELERATEDECELERATE = 2
        const val INTERPOLATOR_DECELERATE = 3
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, R.attr.spbStyle)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}