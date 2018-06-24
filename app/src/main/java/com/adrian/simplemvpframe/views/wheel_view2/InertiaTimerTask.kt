package com.adrian.simplemvpframe.views.wheel_view2

import java.util.*

/**
 * date:2018/6/23
 * author：RanQing
 * description：
 */
class InertiaTimerTask(private val loopView: WheelView, private val velocityY: Float) : TimerTask() {

    private var a: Float = Integer.MAX_VALUE.toFloat()


    override fun run() {
        if (a == Integer.MAX_VALUE.toFloat()) {
            a = if (Math.abs(velocityY) > 2000f) {
                if (velocityY > 0.0f) {
                    2000f
                } else {
                    -2000f
                }
            } else {
                velocityY
            }
        }
        if (Math.abs(a) in 0.0f..20f) {
            loopView.cancelFuture()
            loopView.mHandler!!.sendEmptyMessage(MessageHandler.WHAT_SMOOTH_SCROLL)
            return
        }
        val i = (a * 10f / 1000f).toInt()
        loopView.totalScrollY = loopView.totalScrollY - i
        if (!loopView.isLoop) {
            val itemHeight = loopView.itemHeight
            var top = -loopView.initPosition * itemHeight
            var bottom = (loopView.getItemsCount() - 1 - loopView.initPosition) * itemHeight
            if (loopView.totalScrollY - itemHeight * 0.25 < top) {
                top = loopView.totalScrollY + i
            } else if (loopView.totalScrollY + itemHeight * 0.25 > bottom) {
                bottom = loopView.totalScrollY + i
            }

            if (loopView.totalScrollY <= top) {
                a = 40f
                loopView.totalScrollY = top
            } else if (loopView.totalScrollY >= bottom) {
                loopView.totalScrollY = bottom
                a = -40f
            }
        }
        if (a < 0.0f) {
            a += 20f
        } else {
            a -= 20f
        }
        loopView.mHandler!!.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW)
    }

}