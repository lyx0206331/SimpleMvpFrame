package com.adrian.wheelviewlib.timer

import android.os.Handler
import android.os.Message
import com.adrian.wheelviewlib.view.WheelView

/**
 * author:RanQing
 * date:2018/7/11 0011
 * description:
 **/
class MessageHandler : Handler {

    companion object {
        var WHAT_INVALIDATE_LOOP_VIEW = 1000
        var WHAT_SMOOTH_SCROLL = 2000
        var WHAT_ITEM_SELECTED = 3000
    }

    private var wheelView: WheelView

    constructor(wheelView: WheelView) {
        this.wheelView = wheelView
    }

    override fun handleMessage(msg: Message?) {
        when (msg?.what) {
            WHAT_INVALIDATE_LOOP_VIEW -> {
                wheelView.invalidate()
            }
            WHAT_SMOOTH_SCROLL -> {
                wheelView.smoothScroll(WheelView.ACTION.FLING)
            }
            WHAT_ITEM_SELECTED -> {
                wheelView.onItemSelected()
            }
        }
    }
}