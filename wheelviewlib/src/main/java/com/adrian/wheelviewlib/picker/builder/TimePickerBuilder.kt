package com.adrian.wheelviewlib.picker.builder

import android.content.Context
import com.adrian.wheelviewlib.picker.configure.PickerOptions
import com.adrian.wheelviewlib.picker.listener.OnTimeSelectListener

/**
 * date:2018/8/7 20:03
 * author：RanQing
 * description：
 */
class TimePickerBuilder(context: Context, listener: OnTimeSelectListener) {
    private val mPickerOptions = PickerOptions(PickerOptions.TYPE_PICKER_TIME)

    init {
        mPickerOptions.context = context
        mPickerOptions.timeSelectListener = listener
    }
}