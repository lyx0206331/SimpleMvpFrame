package com.adrian.progressbarlib

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.StyleRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.animation.*
import android.widget.ProgressBar
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.AccelerateDecelerateInterpolator


/**
 * date:2018/7/27
 * author：RanQing
 * description：
 */
open class MaterialProgressBar : ProgressBar {

    companion object {
        const val INTERPOLATOR_ACCELERATE = 0
        const val INTERPOLATOR_LINEAR = 1
        const val INTERPOLATOR_ACCELERATEDECELERATE = 2
        const val INTERPOLATOR_DECELERATE = 3
    }

    private fun logE(msg: String?) {
        Log.e("MaterialProgressBar", msg)
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (isInEditMode) {
            indeterminateDrawable = MaterialProgressDrawable.Builder(context!!, true).build()
            return
        }
        if (context == null) return

        val res: Resources = context.resources
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.MaterialProgressBar, defStyleAttr, 0)

        val color: Int = a.getColor(R.styleable.MaterialProgressBar_spb_color, ContextCompat.getColor(context!!, R.color.spb_default_color))
        val sectionsCount: Int = a.getInteger(R.styleable.MaterialProgressBar_spb_sections_count, res.getInteger(R.integer.spb_default_sections_count))
        val separatorLength: Int = a.getDimensionPixelSize(R.styleable.MaterialProgressBar_spb_stroke_separator_length, res.getDimensionPixelSize(R.dimen.spb_default_stroke_separator_length))
        val strokeWidth: Float = a.getDimension(R.styleable.MaterialProgressBar_spb_stroke_width, res.getDimension(R.dimen.spb_default_stroke_width))
        val speed: Float = a.getFloat(R.styleable.MaterialProgressBar_spb_speed, res.getString(R.string.spb_default_speed).toFloat())
        val speedProgressiveStart: Float = a.getFloat(R.styleable.MaterialProgressBar_spb_progressiveStart_speed, speed)
        val speedProgressiveStop: Float = a.getFloat(R.styleable.MaterialProgressBar_spb_progressiveStop_speed, speed)
        val iInterpolator: Int = a.getInteger(R.styleable.MaterialProgressBar_spb_interpolator, -1)
        val reversed = a.getBoolean(R.styleable.MaterialProgressBar_spb_reversed, res.getBoolean(R.bool.spb_default_reversed))
        val mirrorMode: Boolean = a.getBoolean(R.styleable.MaterialProgressBar_spb_mirror_mode, res.getBoolean(R.bool.spb_default_mirror_mode))
        val colorsId: Int = a.getResourceId(R.styleable.MaterialProgressBar_spb_colors, 0)
        val progressiveStartActivated: Boolean = a.getBoolean(R.styleable.MaterialProgressBar_spb_progressiveStart_activated, res.getBoolean(R.bool.spb_default_progressiveStart_activated))
        val backgroundDrawable: Drawable? = a.getDrawable(R.styleable.MaterialProgressBar_spb_background)
        val generateBackgroundWithColors: Boolean = a.getBoolean(R.styleable.MaterialProgressBar_spb_generate_background_with_colors, false)
        val gradients: Boolean = a.getBoolean(R.styleable.MaterialProgressBar_spb_gradients, false)
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

        val builder: MaterialProgressDrawable.Builder = MaterialProgressDrawable.Builder(context).speed(speed)
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

        val d: MaterialProgressDrawable = builder.build()
        indeterminateDrawable = d
    }

    fun applyStyle(@StyleRes styleResId: Int) {
        val a: TypedArray = context.obtainStyledAttributes(null, R.styleable.MaterialProgressBar, 0, styleResId)

        if (a.hasValue(R.styleable.MaterialProgressBar_spb_color)) {
            setMaterialProgressDrawableColor(a.getColor(R.styleable.MaterialProgressBar_spb_color, 0))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_colors)) {
            val colorsId = a.getResourceId(R.styleable.MaterialProgressBar_spb_colors, 0)
            if (colorsId != 0) {
                val colors = resources.getIntArray(colorsId)
                if (colors != null && colors.isNotEmpty()) {
                    setMaterialProgressDrawableColors(colors)
                }
            }
        }

        if (a.hasValue(R.styleable.MaterialProgressBar_spb_sections_count)) {
            setMaterialProgressDrawableSectionsCount(a.getInteger(R.styleable.MaterialProgressBar_spb_sections_count, 0))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_stroke_separator_length)) {
            setMaterialProgressDrawableSeparatorLength(a.getDimensionPixelSize(R.styleable.MaterialProgressBar_spb_stroke_separator_length, 0))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_stroke_width)) {
            setMaterialProgressDrawableStrokeWidth(a.getDimension(R.styleable.MaterialProgressBar_spb_stroke_width, 0f))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_speed)) {
            setMaterialProgressDrawableSpeed(a.getFloat(R.styleable.MaterialProgressBar_spb_speed, 0f))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_progressiveStart_speed)) {
            setMaterialProgressDrawableProgressiveStartSpeed(a.getFloat(R.styleable.MaterialProgressBar_spb_progressiveStart_speed, 0f))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_progressiveStop_speed)) {
            setMaterialProgressDrawableProgressiveStopSpeed(a.getFloat(R.styleable.MaterialProgressBar_spb_progressiveStop_speed, 0f))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_reversed)) {
            setMaterialProgressDrawableReversed(a.getBoolean(R.styleable.MaterialProgressBar_spb_reversed, false))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_mirror_mode)) {
            setMaterialProgressDrawableMirrorMode(a.getBoolean(R.styleable.MaterialProgressBar_spb_mirror_mode, false))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_progressiveStart_activated)) {
            setProgressiveStartActivated(a.getBoolean(R.styleable.MaterialProgressBar_spb_progressiveStart_activated, false))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_progressiveStart_activated)) {
            setProgressiveStartActivated(a.getBoolean(R.styleable.MaterialProgressBar_spb_progressiveStart_activated, false))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_gradients)) {
            setMaterialProgressDrawableUseGradients(a.getBoolean(R.styleable.MaterialProgressBar_spb_gradients, false))
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_generate_background_with_colors)) {
            if (a.getBoolean(R.styleable.MaterialProgressBar_spb_generate_background_with_colors, false)) {
                setMaterialProgressDrawableBackgroundDrawable(
                        MaterialProgressBarUtils.generateDrawableWithColors(checkIndeterminateDrawable().mColors, checkIndeterminateDrawable().mStrokeWidth))
            }
        }
        if (a.hasValue(R.styleable.MaterialProgressBar_spb_interpolator)) {
            val iInterpolator = a.getInteger(R.styleable.MaterialProgressBar_spb_interpolator, -1)
            val interpolator: Interpolator?
            interpolator = when (iInterpolator) {
                INTERPOLATOR_ACCELERATEDECELERATE -> AccelerateDecelerateInterpolator()
                INTERPOLATOR_DECELERATE -> DecelerateInterpolator()
                INTERPOLATOR_LINEAR -> LinearInterpolator()
                INTERPOLATOR_ACCELERATE -> AccelerateInterpolator()
                else -> null
            }
            if (interpolator != null) {
                setInterpolator(interpolator)
            }
        }
        a.recycle()
    }

    @Synchronized
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isIndeterminate && indeterminateDrawable is MaterialProgressDrawable && !(indeterminateDrawable as MaterialProgressDrawable).isRunning) {
            indeterminateDrawable.draw(canvas)
        }
    }

    private fun checkIndeterminateDrawable(): MaterialProgressDrawable {
        val ret: Drawable = indeterminateDrawable
        if (ret == null || ret !is MaterialProgressDrawable) {
            throw RuntimeException("The drawable is not a MaterialProgressDrawable")
        }
        return ret
    }

    override fun setInterpolator(interpolator: Interpolator?) {
        super.setInterpolator(interpolator)
        if (indeterminateDrawable != null) {
            val ret: Drawable = indeterminateDrawable
            if (ret != null && ret is MaterialProgressDrawable) {
                ret.interpolator = interpolator
            }
        }
    }

    fun setMaterialProgressDrawableInterpolator(interpolator: Interpolator?) {
        checkIndeterminateDrawable().interpolator = interpolator
    }

    fun setMaterialProgressDrawableColors(colors: IntArray) {
        checkIndeterminateDrawable().mColors = colors
    }

    fun setMaterialProgressDrawableColor(@ColorInt color: Int) {
        checkIndeterminateDrawable().setColor(color)
    }

    fun setMaterialProgressDrawableSpeed(speed: Float) {
        checkIndeterminateDrawable().mSpeed = speed
    }

    fun setMaterialProgressDrawableProgressiveStartSpeed(speed: Float) {
        checkIndeterminateDrawable().mProgressiveStartSpeed = speed
    }

    fun setMaterialProgressDrawableProgressiveStopSpeed(speed: Float) {
        checkIndeterminateDrawable().mProgressiveStopSpeed = speed
    }

    fun setMaterialProgressDrawableSectionsCount(sectionsCount: Int) {
        checkIndeterminateDrawable().mSectionsCount = sectionsCount
    }

    fun setMaterialProgressDrawableSeparatorLength(separatorLength: Int) {
        checkIndeterminateDrawable().mSeparatorLength = separatorLength
    }

    fun setMaterialProgressDrawableStrokeWidth(strokeWidth: Float) {
        checkIndeterminateDrawable().mStrokeWidth = strokeWidth
    }

    fun setMaterialProgressDrawableReversed(reversed: Boolean) {
        checkIndeterminateDrawable().mIsReversed = reversed
    }

    fun setMaterialProgressDrawableMirrorMode(mirrorMode: Boolean) {
        checkIndeterminateDrawable().mIsMirrorMode = mirrorMode
    }

    fun setProgressiveStartActivated(progressiveStartActivated: Boolean) {
        checkIndeterminateDrawable().mIsProgressiveStartActivated = progressiveStartActivated
    }

    fun setMaterialProgressDrawableCallbacks(listener: MaterialProgressDrawable.Callbacks) {
        checkIndeterminateDrawable().callbacks = listener
    }

    fun setMaterialProgressDrawableBackgroundDrawable(drawable: Drawable?) {
        checkIndeterminateDrawable().mBackgroundDrawable = drawable
    }

    fun setMaterialProgressDrawableUseGradients(useGradients: Boolean) {
        checkIndeterminateDrawable().mUseGradients = useGradients
    }

    fun progressiveStart() {
        checkIndeterminateDrawable().progressiveStart()
    }

    fun progressiveStop() {
        checkIndeterminateDrawable().progressiveStop()
    }
}