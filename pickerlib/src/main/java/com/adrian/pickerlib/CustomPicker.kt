package com.adrian.pickerlib

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import com.adrian.pickerlib.wheelview.WheelView
import java.util.*

/**
 * date:2018/6/25
 * author：RanQing
 * description：
 */
class CustomPicker : LinearLayout {

    var bgColor: Int = Color.WHITE
    var txtSelectedColor = Color.BLACK
    var txtSelectedSize = 12f
    var txtUnselectedColor = Color.GRAY
    var txtUnselectedSize = 10f
    var dividerColor = Color.GRAY
    var txtLabel: String = "步"
    var txtLabelSize = 10f
    var visibleItemCount = 3

    var isShowLabel: Boolean = false
        set(value) {
            field = value
            if (wheelGroup != null && wheelGroup!!.isNotEmpty()) {
                wheelGroup!![wheelGroup!!.size - 1]!!.setLabel(txtLabel)
            }
        }

    var wheelGroup: Array<WheelView?>? = null

    var groupNum: Int = 1
        set(value) {
            field = value
            if (value > 0) {
                wheelGroup = arrayOfNulls(value)
            }
            for (i in 0 until value) {
                val wheelView = createWheelView()
                addView(wheelView)
                wheelGroup!![i] = wheelView
            }
        }
    var dataGroup: Array<List<String>?>? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        orientation = HORIZONTAL
    }

    fun setData(groupNum: Int, dataGroup: Array<List<String>?>) {
        if (groupNum != dataGroup.size) {
            throw IllegalArgumentException("参数异常")
        }

        wheelGroup = arrayOfNulls(groupNum)
        this.dataGroup = dataGroup
        for (i in 0 until groupNum) {
            val wheelView = createWheelView()
            wheelView.setItems(dataGroup[i])
            addView(wheelView)
            wheelGroup!![i] = wheelView
        }
    }

    private fun createWheelView(): WheelView {
        val wheelView = WheelView(context)
        val lp = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f)
        wheelView.layoutParams = lp
        wheelView.currentItem = 0
        wheelView.setVisibleItemCount(visibleItemCount + 2)
        return wheelView
    }
}