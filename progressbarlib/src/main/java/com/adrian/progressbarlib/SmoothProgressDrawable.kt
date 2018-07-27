package com.adrian.progressbarlib

import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.support.annotation.UiThread
import android.view.animation.Interpolator
import com.sun.org.apache.bcel.internal.generic.FLOAD
import org.jetbrains.annotations.NotNull

/**
 * date:2018/7/27
 * author：RanQing
 * description：
 */
class SmoothProgressDrawable : Drawable, Animatable {

    companion object {
        private const val FRAME_DURATION: Long = 1000 / 60
        private const val OFFSET_PER_FRAME: Float = .01f
    }

    val fBackgroundRect = Rect()
    var callbacks: Callbacks? = null
    var interpolator: Interpolator? = null
        set(value) {
            if (value == null) {
                throw IllegalArgumentException("Interpolator cannot be null")
            }
            field = value
            invalidateSelf()
        }
    var mBounds: Rect? = null
    var mPaint: Paint = Paint()
    var mColors: IntArray? = null
        set(value) {
            if (value == null || value.isEmpty()) {
                throw IllegalArgumentException("colors cannot be null or empty")
            }
            mColorsIndex = 0
            field = value
            refreshLinearGradientOptions()
            invalidateSelf()
        }
    var mColorsIndex: Int = 0
    var mIsRunning: Boolean = false
    var mCurrentOffset: Float = 0f
    var mFinishingOffset: Float = 0f
    var mSeparatorLength: Int = 0
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("SeparatorLength must be >= 0")
            }
            field = value
            invalidateSelf()
        }
    var mSectionsCount: Int = 0
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("SectionsCount must be >= 0")
            }
            field = value
            mMaxOffset = 1f / value
            mCurrentOffset %= mMaxOffset
            refreshLinearGradientOptions()
            invalidateSelf()
        }
    var mSpeed: Float = 0f
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("Speed must be >= 0")
            }
            field = value
            invalidateSelf()
        }
    var mProgressiveStartSpeed: Float = 0f
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("ProgressiveStartSpeed must be >= 0")
            }
            field = value
            invalidateSelf()
        }
    var mProgressiveStopSpeed: Float = 0f
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("ProgressiveStopSpeed must be >= 0")
            }
            field = value
            invalidateSelf()
        }
    var mIsReversed: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            invalidateSelf()
        }
    var mIsMirrorMode: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            invalidateSelf()
        }
    var mMaxOffset: Float = 0f
    var mIsFinishing: Boolean = false
    var mIsProgressiveStartActivated: Boolean = false
    var mStartSection: Int = 0
    var mCurrentSections: Int = 0
    var mStrokeWidth: Float = 0f
        set(value) {
            if (value < 0) {
                throw IllegalArgumentException("StrokeWidth must be >= 0")
            }
            field = value
            mPaint.strokeWidth = value
            invalidateSelf()
        }
    var mBackgroundDrawable: Drawable? = null
    var mUseGradient: Boolean = false
        set(value) {
            if (field == value) return
            field = value
            refreshLinearGradientOptions()
            invalidateSelf()
        }
    var mLinearGradientColors: IntArray? = null
    var mLinearGradientPositions: FloatArray? = null

    interface Callbacks {
        fun onStart()
        fun onStop()
    }

    constructor(callbacks: Callbacks?, @NotNull interpolator: Interpolator, @NotNull mColors: IntArray?,
                mSeparatorLength: Int, mSectionsCount: Int, mSpeed: Float, mProgressiveStartSpeed: Float,
                mProgressiveStopSpeed: Float, mIsReversed: Boolean, mIsMirrorMode: Boolean,
                mIsProgressiveStartActivated: Boolean, mStrokeWidth: Float, mBackgroundDrawable: Drawable, mUseGradient: Boolean) : super() {
        this.callbacks = callbacks
        this.interpolator = interpolator
        this.mColors = mColors
        this.mSeparatorLength = mSeparatorLength
        this.mCurrentSections = mSectionsCount
        this.mSpeed = mSpeed
        this.mProgressiveStartSpeed = mProgressiveStartSpeed
        this.mProgressiveStopSpeed = mProgressiveStopSpeed
        this.mIsReversed = mIsReversed
        this.mIsMirrorMode = mIsMirrorMode
        this.mIsProgressiveStartActivated = mIsProgressiveStartActivated
        this.mStrokeWidth = mStrokeWidth
        this.mBackgroundDrawable = mBackgroundDrawable
        this.mUseGradient = mUseGradient

        mMaxOffset = 1f / mSectionsCount

        mPaint.strokeWidth = mStrokeWidth
        mPaint.style = Paint.Style.STROKE
        mPaint.isDither = false
        mPaint.isAntiAlias = false
    }

    fun setColor(color: Int) {
        mColors = intArrayOf(color)
    }

    private fun refreshLinearGradientOptions() {
        if (mUseGradient) {
            mLinearGradientColors = IntArray(mSectionsCount + 2)
            mLinearGradientPositions = FloatArray(mSectionsCount + 2)
        } else {
            mPaint.shader = null
            mLinearGradientColors = null
            mLinearGradientPositions = null
        }
    }

    @UiThread
    private fun prepareGradient() {
        val xSectionWidth = 1f / mSectionsCount
        var currentIndexColor = mColorsIndex

        mLinearGradientPositions!![0] = 0f
        mLinearGradientPositions!![mLinearGradientPositions!!.lastIndex] = 1f
        var firstColorIndex = currentIndexColor - 1
        if (firstColorIndex < 0) firstColorIndex += mColors!!.size

        mLinearGradientColors!![0] = mColors!![firstColorIndex]

        for (i in 0 until mSectionsCount) {
            val position: Float = interpolator!!.getInterpolation(i * xSectionWidth + mCurrentOffset)
            mLinearGradientPositions!![i + 1] = position
            mLinearGradientColors!![i + 1] = mColors!![currentIndexColor]

            currentIndexColor = (currentIndexColor + 1) % mColors!!.size
        }
        mLinearGradientColors!![mLinearGradientColors!!.lastIndex] = mColors!![currentIndexColor]

        val left: Float = if (mIsReversed) {
            if (mIsMirrorMode) {
                Math.abs(mBounds!!.left - mBounds!!.right) / 2f
            } else {
                mBounds!!.left.toFloat()
            }
        } else {
            mBounds!!.left.toFloat()
        }

        val right: Float = if (mIsMirrorMode) {
            if (mIsReversed) {
                mBounds!!.left.toFloat()
            } else {
                Math.abs(mBounds!!.left - mBounds!!.right) / 2f
            }
        } else {
            mBounds!!.right.toFloat()
        }
        val top: Float = mBounds!!.centerY() - mStrokeWidth / 2
        val bottom: Float = mBounds!!.centerY() + mStrokeWidth / 2
        val linearGradient = LinearGradient(left, top, right, bottom, mLinearGradientColors,
                mLinearGradientPositions, if (mIsMirrorMode) Shader.TileMode.MIRROR else Shader.TileMode.CLAMP)
        mPaint.shader = linearGradient
    }

    override fun draw(canvas: Canvas?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAlpha(alpha: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpacity(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isRunning(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun stop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}