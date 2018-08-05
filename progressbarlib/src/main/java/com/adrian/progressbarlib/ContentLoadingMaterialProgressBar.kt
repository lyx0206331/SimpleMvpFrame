package com.adrian.progressbarlib

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * author:RanQing
 * date:2018/8/1 0001
 * description:
 **/
class ContentLoadingMaterialProgressBar : MaterialProgressBar {

    companion object {
        const val MIN_SHOW_TIME = 500L   //ms
        const val MIN_DELAY = 500L   //ms
    }

    private var mStartTime: Long = -1
    private var mPostedHide: Boolean = false
    private var mPostedShow: Boolean = false
    private var mDismissed: Boolean = false

    private val mDelayedHide = Runnable {
        run {
            mPostedHide = false
            mStartTime = -1
            visibility = View.GONE
        }
    }

    private val mDelayedShow = Runnable {
        run {
            mPostedShow = false
            if (!mDismissed) {
                mStartTime = System.currentTimeMillis()
                visibility = View.VISIBLE
            }
        }
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs, 0)

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        removeCallbacks()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks()
    }

    private fun removeCallbacks() {
        removeCallbacks(mDelayedHide)
        removeCallbacks(mDelayedShow)
    }

    /**
     * Hide the progress view if it is visible.The progress view will not be hidden until it has been
     * shown for at least a minimum show time.If the progress view was not yet visible,cancels showing the progress view.
     */
    fun hide() {
        mDismissed = true
        removeCallbacks(mDelayedShow)
        val diff = System.currentTimeMillis() - mStartTime
        if (diff >= MIN_SHOW_TIME || mStartTime == -1L) {
            //The progress spinner has been shown long enough or was not shown yet.
            //If it wasn't show yet,it will just never be shown.
            visibility = View.GONE
        } else {
            //The progress spinner is shown,but not long enough,so put a delayed message in to hide it
            //when its been shown long enough
            if (!mPostedHide) {
                postDelayed(mDelayedHide, MIN_SHOW_TIME - diff)
                mPostedHide = true
            }
        }
    }

    /**
     * Show the progress view after waiting for a minimum delay. If during that time,hide() is called,
     * the view is never made visible.
     */
    fun show() {
        //reset the start time
        mStartTime = -1
        mDismissed = false
        removeCallbacks(mDelayedHide)
        if (!mPostedShow) {
            postDelayed(mDelayedShow, MIN_DELAY)
            mPostedShow = true
        }
    }
}