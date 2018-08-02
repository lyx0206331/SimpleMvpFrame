package com.adrian.simplemvpframe.progressbar

import android.view.animation.Interpolator

/**
 * date:2018/8/1
 * author：RanQing
 * description：
 */
open class LookupTableInterpolator(private val values: FloatArray) : Interpolator {

    private val stepSize: Float = 1f / (values.size - 1)

    override fun getInterpolation(input: Float): Float {
        if (input >= 1f) return 1f
        if (input <= 0f) return 0f

        //Calculate index - We use min with length - 2 to avoid IndexOutOfBoundsException when we lerp (linearly interpolate) in the retur statement
        val position: Int = Math.min((input * (values.size - 1)).toInt(), values.size - 2)

        //Calculate values to account for small offsets as the lookup table has discrete values
        val quantized: Float = position * stepSize
        val diff: Float = input - quantized
        val weight: Float = diff / stepSize

        //Linearly interpolate between the table values
        return values[position] + weight * (values[position + 1] - values[position])
    }
}