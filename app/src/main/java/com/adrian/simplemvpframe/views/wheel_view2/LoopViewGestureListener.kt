package com.adrian.simplemvpframe.views.wheel_view2

import android.view.GestureDetector
import android.view.MotionEvent

/**
 * author:RanQing
 * date:2018/6/24 0024
 * description:
 **/
class LoopViewGestureListener(val loopView: WheelView) : GestureDetector.SimpleOnGestureListener() {
    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        loopView.scrollBy(velocityY)
        return super.onFling(e1, e2, velocityX, velocityY)
    }
}