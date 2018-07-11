package com.adrian.wheelviewlib.listener

import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import com.adrian.wheelviewlib.view.WheelView

/**
 * author:RanQing
 * date:2018/7/11 0011
 * description:
 **/
class LoopViewGestureListener(val wheelView: WheelView) : SimpleOnGestureListener() {
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        wheelView.scrollBy(velocityY)
        return true
    }
}