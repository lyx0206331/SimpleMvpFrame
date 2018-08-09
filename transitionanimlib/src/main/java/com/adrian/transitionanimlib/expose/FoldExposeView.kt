package com.adrian.transitionanimlib.expose

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.adrian.transitionanimlib.expose.base.ExposeView

/**
 * date:2018/8/9 14:00
 * author：RanQing
 * description：
 */
class FoldExposeView(context: Context, private val foldType: Int = FOLD_TYPE_VERTICAL) : ExposeView(context) {

    companion object {
        const val FOLD_TYPE_HORIZONTAL = 0
        const val FOLD_TYPE_VERTICAL = 1
    }

    override fun animDrawing(canvas: Canvas?, paint: Paint?) {
        when (foldType) {
            FOLD_TYPE_VERTICAL -> canvas?.drawRect(0f, (startExposeY - exposeWidth).toFloat(), screenWidth.toFloat(), (startExposeY + exposeWidth).toFloat(), paint)
            FOLD_TYPE_HORIZONTAL -> canvas?.drawRect((startExposeX - exposeWidth).toFloat(), 0f, (startExposeX + exposeWidth).toFloat(), screenHeight.toFloat(), paint)
        }
    }
}