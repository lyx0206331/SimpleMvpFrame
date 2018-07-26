package com.adrian.wheelviewlib.picker.view

import com.adrian.wheelviewlib.picker.listener.OnOptionsSelectChangeListener
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import com.adrian.wheelviewlib.R
import com.adrian.wheelviewlib.listener.OnItemSelectedListener
import com.adrian.wheelviewlib.view.WheelView


/**
 * date:2018/7/26
 * author：RanQing
 * description：
 */
class WheelOptions(view: View, isRestoreItem: Boolean) {
    private var wv_option1: WheelView? = null
    private var wv_option2: WheelView? = null
    private var wv_option3: WheelView? = null

    private var mOptions1Items: List<*>? = null
    private var mOptions2Items: List<List<*>>? = null
    private var mOptions3Items: List<List<List<*>>>? = null

    private var linkage = true//默认联动
    //    private var isRestoreItem: Boolean = false //切换时，还原第一项
    private var wheelListener_option1: OnItemSelectedListener? = null
    private var wheelListener_option2: OnItemSelectedListener? = null

    private var optionsSelectChangeListener: OnOptionsSelectChangeListener? = null

    //文字的颜色和分割线的颜色
    private var textColorOut: Int = 0
    private var textColorCenter: Int = 0
    private var dividerColor: Int = 0

    private var dividerType: WheelView.DividerType? = null

    // 条目间距倍数
    private var lineSpacingMultiplier: Float = 0.toFloat()

    init {
        wv_option1 = view.findViewById(R.id.options1)
        wv_option2 = view.findViewById(R.id.options2)
        wv_option3 = view.findViewById(R.id.options3)
    }
}