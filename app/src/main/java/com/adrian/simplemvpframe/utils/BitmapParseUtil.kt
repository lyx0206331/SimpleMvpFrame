package com.adrian.simplemvpframe.utils

import android.graphics.Bitmap
import android.view.View

/**
 * author:RanQing
 * date:2018/9/2 0002 20:56
 * description:
 **/
object BitmapParseUtil {
    fun getCacheBitmapFromView(view: View): Bitmap? {
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache(true)
        val drawingCache = view.drawingCache
        var bmp: Bitmap? = null
        if (drawingCache != null) {
            bmp = Bitmap.createBitmap(drawingCache)
            view.isDrawingCacheEnabled = false
        }
        return bmp
    }


    private fun layoutView(view: View, width: Int, height: Int) {
        view.layout(0, 0, width, height)
        val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        view.measure(measuredWidth, measuredHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }
}