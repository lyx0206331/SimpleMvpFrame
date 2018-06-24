package com.adrian.simplemvpframe.views.wheel_view2

import java.util.*

/**
 * author:RanQing
 * date:2018/6/25 0025
 * description:
 **/
class SmoothScrollTimerTask(val loopView: WheelView, val offset: Int) : TimerTask() {

    var realTotalOffset: Int = Integer.MAX_VALUE
    var realOffset: Int = 0

    override fun run() {
        if (realTotalOffset == Integer.MAX_VALUE) {
            realTotalOffset = offset
        }
        //把要滚动的范围细分成10小份，按10小份单位来重绘
        realOffset = (realTotalOffset.toFloat() * 0.1f).toInt()

        if (realOffset == 0) {
            realOffset = if (realTotalOffset < 0) {
                -1
            } else {
                1
            }
        }

        if (Math.abs(realTotalOffset) <= 1) {
            loopView.cancelFuture()
            loopView.mHandler!!.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED)
        } else {
            loopView.totalScrollY = loopView.totalScrollY + realOffset

            //这里如果不是循环模式，则点击空白位置需要回滚，不然就会出现选到－1 item的 情况
            if (!loopView.isLoop) {
                val itemHeight = loopView.itemHeight
                val top = -loopView.initPosition as Float * itemHeight
                val bottom = (loopView.getItemsCount() - 1 - loopView.initPosition) as Float * itemHeight
                if (loopView.totalScrollY <= top || loopView.totalScrollY >= bottom) {
                    loopView.totalScrollY = loopView.totalScrollY - realOffset
                    loopView.cancelFuture()
                    loopView.mHandler!!.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED)
                    return
                }
            }
            loopView.mHandler!!.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW)
            realTotalOffset -= realOffset
        }
    }
}