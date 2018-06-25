package com.adrian.wheelviewpicker.view

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