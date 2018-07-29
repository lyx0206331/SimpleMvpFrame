package com.adrian.progressbarlib

import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.support.annotation.UiThread
import android.view.animation.Interpolator
import org.jetbrains.annotations.NotNull
import java.util.*

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
    var mIsNewTurn: Boolean = false
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
        set(value) {
            if (field == value) return
            field = value
            invalidateSelf()
        }
    var mUseGradients: Boolean = false
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
                mIsProgressiveStartActivated: Boolean, mStrokeWidth: Float, mBackgroundDrawable: Drawable, mUseGradients: Boolean) : super() {
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
        this.mUseGradients = mUseGradients

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
        if (mUseGradients) {
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

    @UiThread
    private fun drawStrokes(canvas: Canvas) {
        if (mIsReversed) {
            canvas.translate(mBounds!!.width().toFloat(), 0f)
            canvas.scale(-1f, 1f)
        }

        var prevValue: Float = 0f
        var boundsWidth: Int = mBounds!!.width()
        if (mIsMirrorMode) boundsWidth /= 2
        var width: Int = boundsWidth + mSeparatorLength + mSectionsCount
        var centerY = mBounds!!.centerY()
        var xSectionWidth = 1f / mSectionsCount

        var startX: Float
        var endX: Float
        var firstX: Float = 0f
        var lastX: Float = 0f
        var prev: Float
        var end: Float
        var spaceLength: Float
        var xOffset: Float
        var ratioSectionWidth: Float
        var sectionWidth: Float
        var drawLength: Float
        var currentIndexColor = mColorsIndex

        if (mStartSection == mCurrentSections && mCurrentSections == mSectionsCount) firstX = canvas.width.toFloat()

        for (i in 0..mCurrentSections) {
            xOffset = xSectionWidth * i + mCurrentOffset
            prev = Math.max(0f, xOffset - xSectionWidth)
            ratioSectionWidth = Math.abs(interpolator!!.getInterpolation(prev) - interpolator!!.getInterpolation(Math.min(xOffset, 1f)))
            sectionWidth = width * ratioSectionWidth

            spaceLength = if (sectionWidth + prev < width) Math.min(sectionWidth, mSeparatorLength.toFloat()) else 0f

            drawLength = if (sectionWidth > spaceLength) sectionWidth - spaceLength else 0f
            end = prevValue + drawLength
            if (end > prevValue && i >= mStartSection) {
                val xFinishingOffset: Float = interpolator!!.getInterpolation(Math.min(mFinishingOffset, 1f))
                startX = Math.max(xFinishingOffset * width, Math.min(boundsWidth.toFloat(), prevValue))
                endX = Math.min(boundsWidth.toFloat(), end)
                drawLine(canvas, boundsWidth, startX, centerY.toFloat(), endX, centerY.toFloat(), currentIndexColor)
                if (i == mStartSection) {   //first loop
                    firstX = startX - mSeparatorLength
                }
            }
            if (i == mCurrentSections) {
                lastX = prevValue + sectionWidth    //keep the separator effect
            }

            prevValue = end + spaceLength
            currentIndexColor = incrementColor(currentIndexColor)
        }

        drawBackgroundIfNeed(canvas, firstX, lastX)
    }

    @UiThread
    private fun drawLine(canvas: Canvas, canvasWidth: Int, startX: Float, startY: Float, stopX: Float, stopY: Float, currentIndexColor: Int) {
        mPaint.color = mColors!![currentIndexColor]

        if (mIsMirrorMode) {
            canvas.drawLine(startX, startY, stopX, stopY, mPaint)
        } else {
            if (mIsReversed) {
                canvas.drawLine(canvasWidth + startX, startY, canvasWidth + stopX, stopY, mPaint)
                canvas.drawLine(canvasWidth - startX, startY, canvasWidth - stopX, stopY, mPaint)
            } else {
                canvas.drawLine(startX, stopY, stopX, stopY, mPaint)
                canvas.drawLine(canvasWidth * 2 - startX, startY, canvasWidth * 2 - stopX, stopY, mPaint)
            }
        }
    }

    @UiThread
    private fun incrementColor(colorIndex: Int): Int {
        val i = colorIndex + 1
        return if (i >= mColors!!.size) 0 else i
    }

    @UiThread
    private fun decrementColor(colorIndex: Int): Int {
        val i = colorIndex - 1
        return if (i < 0) mColors!!.lastIndex else i
    }

    /**
     * start the animation with the first color.
     * Calls progressiveStart(0)
     */
    @UiThread
    private fun progressiveStart() {
        progressiveStart(0)
    }

    /**
     * start the animation from a given color.
     */
    @UiThread
    private fun progressiveStart(index: Int) {
        resetProgressiveStart(index)
        start()
    }

    @UiThread
    private fun resetProgressiveStart(index: Int) {
        checkColorIndex(index)

        mCurrentOffset = 0f
        mIsFinishing = false
        mFinishingOffset = 0f
        mStartSection = 0
        mCurrentSections = 0
        mColorsIndex = index

    }

    private fun checkColorIndex(index: Int) {
        if (index < 0 || index >= mColors!!.size) {
            throw IllegalArgumentException(String.format(Locale.US, "Index %d not valid", index))
        }
    }

    /**
     * finish the animation by animating the remaining sections.
     */
    @UiThread
    private fun progressiveStop() {
        mIsFinishing = true
        mStartSection = 0
    }

    @UiThread
    private fun drawBackgroundIfNeed(canvas: Canvas, firstX: Float, lastX: Float) {
        if (mBackgroundDrawable == null) return

        fBackgroundRect.top = ((canvas.height - mStrokeWidth) / 2).toInt()
        fBackgroundRect.bottom = ((canvas.height + mStrokeWidth) / 2).toInt()
        fBackgroundRect.left = 0
        fBackgroundRect.right = if (mIsMirrorMode) canvas.width / 2 else canvas.width
        mBackgroundDrawable!!.bounds = fBackgroundRect

        //draw the background if the animation is over
        if (!mIsRunning) {
            if (mIsMirrorMode) {
                canvas.save()
                canvas.translate(canvas.width / 2f, 0f)
                drawBackground(canvas, 0f, fBackgroundRect.width().toFloat())
                canvas.scale(-1f, 1f)
                drawBackground(canvas, 0f, fBackgroundRect.width().toFloat())
                canvas.restore()
            } else {
                drawBackground(canvas, 0f, fBackgroundRect.width().toFloat())
            }
            return
        }

        if (!mIsFinishing && !isStarting()) return

        var tmpfirstX = firstX
        var tmpLastX = lastX
        if (tmpfirstX > tmpLastX) {
            val temp: Float = tmpfirstX
            tmpfirstX = tmpLastX
            tmpLastX = temp
        }

        if (tmpfirstX > 0) {
            if (mIsMirrorMode) {
                canvas.save()
                canvas.translate(canvas.width / 2f, 0f)
                if (mIsReversed) {
                    drawBackground(canvas, 0f, tmpfirstX)
                    canvas.scale(-1f, 1f)
                    drawBackground(canvas, 0f, tmpfirstX)
                } else {
                    drawBackground(canvas, canvas.width / 2 - tmpfirstX, canvas.width / 2f)
                    canvas.scale(-1f, 1f)
                    drawBackground(canvas, canvas.width / 2 - tmpfirstX, canvas.width / 2f)
                }
                canvas.restore()
            } else {
                drawBackground(canvas, 0f, tmpfirstX)
            }
        }

        if (tmpLastX <= canvas.width) {
            if (mIsMirrorMode) {
                canvas.save()
                canvas.translate(canvas.width / 2f, 0f)
                if (mIsReversed) {
                    drawBackground(canvas, tmpLastX, canvas.width / 2f)
                    canvas.scale(-1f, 1f)
                    drawBackground(canvas, tmpLastX, canvas.width / 2f)
                } else {
                    drawBackground(canvas, 0f, canvas.width / 2 - tmpLastX)
                    canvas.scale(-1f, 1f)
                    drawBackground(canvas, 0f, canvas.width / 2 - tmpLastX)
                }
                canvas.restore()
            } else {
                drawBackground(canvas, tmpLastX, canvas.width.toFloat())
            }
        }
    }

    @UiThread
    private fun drawBackground(canvas: Canvas, fromX: Float, toX: Float) {
        val count = canvas.save()
        canvas.clipRect(fromX, (canvas.height - mStrokeWidth) / 2, toX, (canvas.height + mStrokeWidth) / 2)
        mBackgroundDrawable!!.draw(canvas)
        canvas.restoreToCount(count)
    }

    private fun isStarting(): Boolean {
        return mCurrentSections < mSectionsCount
    }

    override fun draw(canvas: Canvas?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    override fun isRunning(): Boolean {
        return mIsRunning
    }

    override fun start() {
        if (mIsProgressiveStartActivated) {
            resetProgressiveStart(0)
        }
        if (mIsRunning) return

        callbacks?.onStart()

        scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION)
        invalidateSelf()
    }

    override fun stop() {
        if (!mIsRunning) return

        callbacks?.onStop()

        mIsRunning = false

        unscheduleSelf(mUpdater)
    }

    override fun scheduleSelf(what: Runnable?, `when`: Long) {
        mIsRunning = true
        super.scheduleSelf(what, `when`)
    }

    private val mUpdater: Runnable = Runnable {
        run {
            if (mIsFinishing) {
                mFinishingOffset += OFFSET_PER_FRAME * mProgressiveStopSpeed
                mCurrentOffset += OFFSET_PER_FRAME * mProgressiveStopSpeed
                if (mFinishingOffset >= 1f) {
                    stop()
                }
            } else if (isStarting()) {
                mCurrentOffset += OFFSET_PER_FRAME * mProgressiveStartSpeed
            } else {
                mCurrentOffset += OFFSET_PER_FRAME * mSpeed
            }

            if (mCurrentOffset >= mMaxOffset) {
                mIsNewTurn = true
                mCurrentOffset -= mMaxOffset
            }

            if (mIsRunning) {
                scheduleSelf(mUpdater, SystemClock.uptimeMillis() + FRAME_DURATION)
            }

            invalidateSelf()
        }
    }
}