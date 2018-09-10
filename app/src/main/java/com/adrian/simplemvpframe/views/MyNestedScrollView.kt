package com.adrian.simplemvpframe.views

import android.content.Context
import android.support.v4.widget.NestedScrollView
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View

/**
 * date:2018/9/10 16:45
 * author：RanQing
 * description：
 */
class MyNestedScrollView : NestedScrollView {

    private val velocityTracker: VelocityTracker by lazy {
        VelocityTracker.obtain()
    }

    var velocityChangedListener: IScrollVelocityChangedListener? = null

    private fun logE(msg: String) {
        Log.e("MyNestScrollView", msg)
    }

    interface IScrollVelocityChangedListener {
        fun onPressUpVelocityChanged(velocityX: Float, velocityY: Float)
        fun onFlingVelocityChanged(velocityX: Float, velocityY: Float)
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (ev == null) return super.onTouchEvent(ev)
//        logE("onTouchEvent ev:${ev?.action}")
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
                velocityTracker.addMovement(ev)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                velocityTracker.computeCurrentVelocity(1000)
                velocityChangedListener?.onPressUpVelocityChanged(velocityTracker.xVelocity, velocityTracker.yVelocity)
            }
        }
        return super.onTouchEvent(ev)
    }

    override fun onDetachedFromWindow() {
        velocityTracker.recycle()
        super.onDetachedFromWindow()
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
//        logE("onNestedFling velocityY:$velocityY")
        return super.onNestedFling(target, velocityX, velocityY, consumed)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
//        logE("onNestedPreScroll dy:$dy")
        super.onNestedPreScroll(target, dx, dy, consumed)
    }

    override fun onStopNestedScroll(target: View) {
//        logE("onStopNestedScroll")
        super.onStopNestedScroll(target)
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?, type: Int): Boolean {
//        logE("dispatchNestedScroll dyConsumed:$dyConsumed dyUnconsumed:$dyUnconsumed")
        return super.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
//        logE("dispatchNestedPreFling velocityY:$velocityY")
        return super.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun fling(velocityY: Int) {
//        logE("fling velocityY:$velocityY")
        super.fling(velocityY)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
//        logE("dispatchNestedFling velocityY:$velocityY")
        return super.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?, type: Int): Boolean {
        logE("dispatchNestedPreScroll dy:$dy")
        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int) {
//        logE("onNestedScroll dyConsumed:$dyConsumed dyUnconsumed:$dyUnconsumed")
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed)
    }

    override fun stopNestedScroll() {
//        logE("stopNestedScroll")
        super.stopNestedScroll()
    }

    override fun stopNestedScroll(type: Int) {
        logE("stopNestedScroll type:$type")
        super.stopNestedScroll(type)
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
//        logE("onGenericMotionEvent event:${event?.action}")
        return super.onGenericMotionEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        logE("onInterceptTouchEvent ev:${ev?.action}")
        return super.onInterceptTouchEvent(ev)
    }
}