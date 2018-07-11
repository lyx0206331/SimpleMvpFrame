package com.adrian.wheelviewlib.timer

import com.adrian.wheelviewlib.view.WheelView
import java.util.*

/**
 * author:RanQing
 * date:2018/7/11 0011
 * description:
 **/
class SmoothScrollTimerTask : TimerTask {

    private var realTotalOffset = 0
    private var realOffset = 0
    private var offset = 0
    private var wheelView: WheelView

    constructor(wheelView: WheelView, offset: Int) : super() {
        this.offset = offset
        this.wheelView = wheelView
        realTotalOffset = Int.MAX_VALUE
        realOffset = 0
    }

    override fun run() {

    }
}