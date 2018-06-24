package com.adrian.simplemvpframe.views.wheel_view2

/**
 * author:RanQing
 * date:2018/6/25 0025
 * description:
 **/
class OnItemSelectedRunnable(val loopView: WheelView) : Runnable {
    override fun run() {
        loopView.onItemSelectedListener!!.onItemSelected(loopView.selectedItem)
    }
}