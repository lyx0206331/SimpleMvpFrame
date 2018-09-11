package com.adrian.heartanimation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Checkable

/**
 * date:2018/9/11 19:52
 * author：RanQing
 * description：
 */
class HeartView : View, Checkable {


    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun isChecked(): Boolean {
        return false
    }

    override fun toggle() {

    }

    override fun setChecked(p0: Boolean) {

    }
}