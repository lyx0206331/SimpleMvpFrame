package com.adrian.wheelviewlib.view

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * author:RanQing
 * date:2018/7/10 0010
 * description:
 **/
class WheelView : View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}