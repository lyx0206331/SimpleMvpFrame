package com.adrian.progressbarlib

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.shapes.Shape

class ColorsShape : Shape {

    var mStrokeWidth: Float
    var mColors: IntArray

    constructor(strokeWidth: Float, colors: IntArray) {
        mStrokeWidth = strokeWidth
        mColors = colors
    }

    override fun draw(p0: Canvas?, p1: Paint?) {
        val ratio = 1f / mColors.size
        var i = 0
        p1?.strokeWidth = mStrokeWidth
        mColors.forEach {
            p1?.color = it
            p0?.drawLine(i * ratio * width, height / 2, ++i * ratio * width, height / 2, p1)
        }
    }
}