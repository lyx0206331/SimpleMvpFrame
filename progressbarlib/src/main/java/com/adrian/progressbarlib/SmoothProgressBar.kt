package com.adrian.progressbarlib

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.animation.*
import android.widget.ProgressBar

/**
 * date:2018/7/27
 * author：RanQing
 * description：
 */
class SmoothProgressBar : ProgressBar {

    companion object {
        const val INTERPOLATOR_ACCELERATE = 0
        const val INTERPOLATOR_LINEAR = 1
        const val INTERPOLATOR_ACCELERATEDECELERATE = 2
        const val INTERPOLATOR_DECELERATE = 3
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, R.attr.spbStyle)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (isInEditMode) {
            indeterminateDrawable = SmoothProgressDrawable.Builder(context!!, true).build()
            return
        }
        if (context == null) return

        val res: Resources = context.resources
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SmoothProgressBar, defStyleAttr, 0)

        val color: Int = a.getColor(R.styleable.SmoothProgressBar_spb_color, ContextCompat.getColor(context!!, R.color.spb_default_color))
        val sectionsCount: Int = a.getInteger(R.styleable.SmoothProgressBar_spb_sections_count, res.getInteger(R.integer.spb_default_sections_count))
        val separatorLength: Int = a.getDimensionPixelSize(R.styleable.SmoothProgressBar_spb_stroke_separator_length, res.getDimensionPixelSize(R.dimen.spb_default_stroke_separator_length))
        val strokeWidth: Float = a.getDimension(R.styleable.SmoothProgressBar_spb_stroke_width, res.getDimension(R.dimen.spb_default_stroke_width))
        val speed: Float = a.getFloat(R.styleable.SmoothProgressBar_spb_speed, res.getString(R.string.spb_default_speed).toFloat())
        val speedProgressiveStart: Float = a.getFloat(R.styleable.SmoothProgressBar_spb_progressiveStart_speed, speed)
        val speedProgressiveStop: Float = a.getFloat(R.styleable.SmoothProgressBar_spb_progressiveStop_speed, speed)
        val iInterpolator: Int = a.getInteger(R.styleable.SmoothProgressBar_spb_interpolator, -1)
        val reversed = a.getBoolean(R.styleable.SmoothProgressBar_spb_reversed, res.getBoolean(R.bool.spb_default_reversed))
        val mirrorMode: Boolean = a.getBoolean(R.styleable.SmoothProgressBar_spb_mirror_mode, res.getBoolean(R.bool.spb_default_mirror_mode))
        val colorsId: Int = a.getResourceId(R.styleable.SmoothProgressBar_spb_colors, 0)
        val progressiveStartActivated: Boolean = a.getBoolean(R.styleable.SmoothProgressBar_spb_progressiveStart_activated, res.getBoolean(R.bool.spb_default_progressiveStart_activated))
        val backgroundDrawable: Drawable = a.getDrawable(R.styleable.SmoothProgressBar_spb_background)
        val generateBackgroundWithColors: Boolean = a.getBoolean(R.styleable.SmoothProgressBar_spb_generate_background_with_colors, false)
        val gradients: Boolean = a.getBoolean(R.styleable.SmoothProgressBar_spb_gradients, false)
        a.recycle()

        var interpolator: Interpolator? = null
        if (iInterpolator == -1) interpolator = getInterpolator()
        if (interpolator == null) {
            interpolator = when (iInterpolator) {
                INTERPOLATOR_ACCELERATEDECELERATE -> AccelerateDecelerateInterpolator()
                INTERPOLATOR_DECELERATE -> DecelerateInterpolator()
                INTERPOLATOR_LINEAR -> LinearInterpolator()
                else -> AccelerateInterpolator()
            }
        }

        var colors: IntArray? = null
        if (colorsId != 0) {
            colors = res.getIntArray(colorsId)
        }

        val builder: SmoothProgressDrawable.Builder = SmoothProgressDrawable.Builder(context).speed(speed)
                .progressiveStartSpeed(speedProgressiveStart).progressiveStopSpeed(speedProgressiveStop)
                .interpolator(interpolator).sectionsCount(sectionsCount).separatorLength(separatorLength)
                .strokeWidth(strokeWidth).reversed(reversed).mirrorMode(mirrorMode).progressiveStart(progressiveStartActivated)
                .gradients(gradients)

        if (backgroundDrawable != null) {
            builder.backgroundDrawable(backgroundDrawable)
        }
        if (generateBackgroundWithColors) {
            builder.generateBackgroundUsingColors()
        }
        if (colors != null && colors.isNotEmpty()) {
            builder.colors(colors)
        } else {
            builder.color(color)
        }

        val d: SmoothProgressDrawable = builder.build()
        indeterminateDrawable = d
    }
}