package com.adrian.wheelviewlib.picker.view

import android.content.Context
import android.view.View
import com.adrian.wheelviewlib.picker.configure.PickerOptions

/**
 * date:2018/8/8 17:20
 * author：RanQing
 * description：
 */
class TimePickerView : BasePickerView, View.OnClickListener {
    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    constructor(pickerOptions: PickerOptions) : super(pickerOptions.context) {
        mPickerOptions = pickerOptions
        initView(pickerOptions.context)
    }

    private fun initView(context: Context?) {

    }
}