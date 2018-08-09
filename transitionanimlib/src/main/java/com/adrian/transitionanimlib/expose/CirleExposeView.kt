package com.adrian.transitionanimlib.expose

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.adrian.transitionanimlib.expose.base.ExposeView

/**
 * date:2018/8/9 13:58
 * author：RanQing
 * description：
 */
class CirleExposeView(context: Context?) : ExposeView(context) {
    override fun animDrawing(canvas: Canvas?, paint: Paint?) {
        canvas?.drawCircle(startExposeX.toFloat(), startExposeY.toFloat(), exposeWidth.toFloat(), paint)
    }
}