package com.adrian.progressbarlib

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.shapes.Shape

class ColorsShape(private val strokeWidth: Float, private val colors: IntArray) : Shape() {
    override fun draw(p0: Canvas?, p1: Paint?) {
        val ratio = 1f / colors.size
        var i = 0
        p1?.strokeWidth = strokeWidth
        colors.forEach {
            p1?.color = it
            p0?.drawLine(i * ratio * width, height / 2, ++i * ratio * width, height / 2, p1)
        }
    }
}