package com.adrian.wheelviewlib.picker.view

import android.content.Context
import android.view.View
import com.adrian.wheelviewlib.picker.configure.PickerOptions

/**
 * date:2018/7/26
 * author：RanQing
 * description：
 */
class OptionsPickerView<T> : BasePickerView, View.OnClickListener {

    constructor(pickerOptions: PickerOptions) : super(pickerOptions.context!!) {
        mPickerOptions = pickerOptions
    }

    override fun onClick(v: View?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}