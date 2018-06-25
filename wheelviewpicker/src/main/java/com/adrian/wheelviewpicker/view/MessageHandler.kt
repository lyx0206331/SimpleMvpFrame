package com.adrian.wheelviewpicker.view

import android.os.Handler
import android.os.Message

/**
 * author:RanQing
 * date:2018/6/24 0024
 * description:
 **/
class MessageHandler(val loopView: WheelView) : Handler() {

    companion object {
        val WHAT_INVALIDATE_LOOP_VIEW: Int = 1000
        val WHAT_SMOOTH_SCROLL = 2000
        val WHAT_ITEM_SELECTED = 3000
    }

    override fun handleMessage(msg: Message?) {
        when (msg?.what) {
            WHAT_INVALIDATE_LOOP_VIEW -> loopView.invalidate()
            WHAT_SMOOTH_SCROLL -> loopView.smoothScroll(WheelView.ACTION.FLING)
            WHAT_ITEM_SELECTED -> loopView.onItemSelected()
        }
    }
}