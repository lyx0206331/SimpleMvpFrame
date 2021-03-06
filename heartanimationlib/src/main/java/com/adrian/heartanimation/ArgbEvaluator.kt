package com.adrian.heartanimation

import android.animation.TypeEvaluator

/**
 * date:2018/9/11 19:36
 * author：RanQing
 * description：
 */
object ArgbEvaluator : TypeEvaluator<Int> {

    /**
     * This function returns the calculated in-between value for a color
     * given integers that represent the start and end values in the four
     * bytes of the 32-bit int. Each channel is separately linearly interpolated
     * and the resulting calculated values are recombined into the return value.
     *
     * @param fraction The fraction from the starting to the ending values
     * @param startValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @param endValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @return A value that is calculated to be the linearly interpolated
     * result, derived by separating the start and end values into separate
     * color channels and interpolating each one separately, recombining the
     * resulting values in the same way.
     */
    override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = startValue.shr(24).and(0xff)
        val startR = startValue.shr(16).and(0xff)
        val startG = startValue.shr(8).and(0xff)
        val startB = startValue.and(0xff)

        val endA = endValue.shr(24).and(0xff)
        val endR = endValue.shr(16).and(0xff)
        val endG = endValue.shr(8).and(0xff)
        val endB = endValue.and(0xff)

        return ((startA + (fraction * (endA - startA))).toInt() shl 24) or
                ((startR + (fraction * (endR - startR))).toInt() shl 16) or
                ((startG + (fraction * (endG - startG))).toInt() shl 8) or
                (startB + (fraction * (endB - startB))).toInt()
    }
}