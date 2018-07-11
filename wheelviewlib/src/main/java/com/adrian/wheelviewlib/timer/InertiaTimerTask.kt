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
        currentVelocityY = MAX_VALUE.toFloat()
    }

    override fun run() {

    }
}