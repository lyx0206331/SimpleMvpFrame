package com.adrian.transitionanimlib.util

import android.graphics.Bitmap
import android.util.Log
import android.view.View

/**
 * date:2018/8/9 16:07
 * author：RanQing
 * description：
 */
object BitmapUtils {

    fun createBitmap(view: View, width: Int, height: Int, needOnLayout: Boolean): Bitmap? {
        var bitmap: Bitmap? = null
        if (view != null) {
            view.clearFocus()
            view.isPressed = false

            val willNotCache = view.willNotCacheDrawing()
            view.setWillNotCacheDrawing(false)

            // Reset the drawing cache background color to fully transparent
            // for the showDuration of this operation
            val color = view.drawingCacheBackgroundColor
            view.drawingCacheBackgroundColor = 0
            val alpha = view.alpha
            view.alpha = 1.0f

            if (color != 0) {
                view.destroyDrawingCache()
            }

            val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            view.measure(widthSpec, heightSpec)
            if (needOnLayout) {
                view.layout(0, 0, width, height)
            }
            view.buildDrawingCache()
            val cacheBitmap = view.drawingCache
            if (cacheBitmap == null) {
                Log.e("view.ProcessImageToBlur", "failed getViewBitmap($view)",
                        RuntimeException())
                return null
            }
            bitmap = Bitmap.createBitmap(cacheBitmap)
            // Restore the view
            view.alpha = alpha
            view.destroyDrawingCache()
            view.setWillNotCacheDrawing(willNotCache)
            view.drawingCacheBackgroundColor = color
        }
        return bitmap
    }
}