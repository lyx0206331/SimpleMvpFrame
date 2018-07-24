package com.adrian.wheelviewlib.timer

import com.adrian.wheelviewlib.view.WheelView
import java.util.*
import kotlin.Int.Companion.MAX_VALUE

/**
 * author:RanQing
 * date:2018/7/11 0011
 * description:
 **/
class InertiaTimerTask : TimerTask {

    private var currentVelocityY = 0f
    private var firstVelocityY = 0f
    private var wheelView: WheelView

    constructor(wheelView: WheelView, velocityY: Float) : super() {
        this.firstVelocityY = velocityY
        this.wheelView = wheelView
        currentVelocityY = Float.MAX_VALUE
    }

    override fun run() {
        //防止闪动，去速度做一个限制
        if (currentVelocityY == Float.MAX_VALUE) {
            currentVelocityY = if (Math.abs(firstVelocityY) > 2000f) {
                if (firstVelocityY > 0) 2000f else -2000f
            } else {
                firstVelocityY
            }
        }

        //发送handler消息处理平顺停止滚动逻辑
        if (Math.abs(currentVelocityY) > 0f && Math.abs(currentVelocityY) <= 20f) {
            wheelView.cancelFuture()
            wheelView.handler.sendEmptyMessage(MessageHandler.WHAT_SMOOTH_SCROLL)
            return
        }
    }
}