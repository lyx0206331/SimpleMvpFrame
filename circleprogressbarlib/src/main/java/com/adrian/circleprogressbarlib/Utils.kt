package com.adrian.circleprogressbarlib

import android.content.Context

/**
 * date:2018/8/27 16:57
 * author：RanQing
 * description：
 */
object Utils {

    fun dip2px(context: Context, dpValue: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dpValue * scale + .5f
    }
}