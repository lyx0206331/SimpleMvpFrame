package com.adrian.wheelviewlib.timer

import com.adrian.wheelviewlib.view.WheelView
import java.util.*

/**
 * author:RanQing
 * date:2018/7/11 0011
 * description:
 **/
class SmoothScrollTimerTask(private val wheelView: WheelView, private val offset: Int) : TimerTask() {

    private var realTotalOffset = 0
    private var realOffset = 0

    init {
        realTotalOffset = Int.MAX_VALUE
        realOffset = 0
    }

    override fun run() {
        if (realTotalOffset == Int.MAX_VALUE) {
            realTotalOffset = offset
        }
        //把要滚动的范围细分成10小份，按10小份单位来重绘
        realOffset = (realTotalOffset * .1f).toInt()

        if (realOffset == 0) {
            realOffset = if (realTotalOffset < 0) -1 else 1
        }

        if (Math.abs(realTotalOffset) <= 1) {
            wheelView.cancelFuture()
            wheelView.handler.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED)
        } else {
            wheelView.totalScrollY = wheelView.totalScrollY + realOffset

            //如果不是循环模式,点击空白位置需要回滚,不然会出现选到-1位置的情况
            if (!wheelView.isLoop) {
                val itemHeight = wheelView.itemHeight
                val top = -wheelView.initPosition * itemHeight
                val bottom = (wheelView.getItemsCount() - 1 - wheelView.initPosition) * itemHeight

                if (wheelView.totalScrollY <= top || wheelView.totalScrollY >= bottom) {
                    wheelView.totalScrollY -= realOffset
                    wheelView.cancelFuture()
                    wheelView.handler.sendEmptyMessage(MessageHandler.WHAT_ITEM_SELECTED)
                    return
                }
            }
            wheelView.handler.sendEmptyMessage(MessageHandler.WHAT_INVALIDATE_LOOP_VIEW)
            realTotalOffset -= realOffset
        }
    }
}