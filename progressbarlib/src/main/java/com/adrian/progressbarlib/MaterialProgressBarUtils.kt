package com.adrian.progressbarlib

import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import java.util.*

object MaterialProgressBarUtils {

    fun generateDrawableWithColors(colors: IntArray?, strokeWidth: Float): Drawable? {
        return if (colors == null || colors.isEmpty()) null else ShapeDrawable(ColorsShape(strokeWidth, colors))
    }

    fun checkSpeed(speed: Float) {
        if (speed <= 0f) {
            throw IllegalArgumentException("Speed must be larger than 0")
        }
    }

    fun checkColors(colors: IntArray?) {
        if (colors == null || colors.isEmpty()) {
            throw IllegalArgumentException("You must provide at least 1 color")
        }
    }

    fun checkAngle(angle: Int) {
        if (angle < 0 || angle > 360) {
            throw IllegalArgumentException(String.format(Locale.US, "Illegal angle %d: must be >=0 and <= 360", angle))
        }
    }

    fun checkPositiveOrZero(number: Float, name: String) {
        if (number < 0) {
            throw IllegalArgumentException(String.format(Locale.US, "%s %d must be positive", name, number))
        }
    }

    fun checkPositive(number: Float, name: String) {
        if (number <= 0) {
            throw IllegalArgumentException(String.format(Locale.US, "%s must not be null", name))
        }
    }

    fun checkNotNull(any: Any?, name: String) {
        if (any == null) {
            throw IllegalArgumentException(String.format(Locale.US, "%s must be not null", name))
        }
    }
}