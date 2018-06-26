package com.adrian.pickerlib

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.adrian.pickerlib.wheelview.OnItemSelectedListener
import com.adrian.pickerlib.wheelview.WheelView
import java.util.*

/**
 * date:2018/6/25
 * author：RanQing
 * description：
 */
class CustomPicker : LinearLayout {

    var ctx: Context? = null
    var view: LinearLayout? = null
    var bgColor: Int = Color.WHITE
    var txtSelectedColor = Color.BLACK
    var txtSelectedSize = 12f
    var txtUnselectedColor = Color.GRAY
    var txtUnselectedSize = 10f
    var dividerColor = Color.GRAY
    var txtLabel: String = "步"
    var txtLabelSize = 10f
    var visibleCount = 3
    var defaultSelected: Int = 0

    var isShowLabel: Boolean = false
        set(value) {
            field = value
            if (wheelGroup != null && wheelGroup!!.isNotEmpty()) {
                wheelGroup!![wheelGroup!!.size - 1]!!.setLabel(txtLabel)
            }
        }

    private var wheelGroup: Array<WheelView?>? = arrayOfNulls(5)

    private var dataGroup: Array<List<String>?>? = null

    var selectedDatas: Array<String?>? = null

    private var wv0: WheelView? = null
    private var wv1: WheelView? = null
    private var wv2: WheelView? = null
    private var wv3: WheelView? = null
    private var wv4: WheelView? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_custom_picker, this)

        wv0 = findViewById(R.id.wv_0)
        wv1 = findViewById(R.id.wv_1)
        wv2 = findViewById(R.id.wv_2)
        wv3 = findViewById(R.id.wv_3)
        wv4 = findViewById(R.id.wv_4)

        wheelGroup!![0] = wv0
        wheelGroup!![1] = wv1
        wheelGroup!![2] = wv2
        wheelGroup!![3] = wv3
        wheelGroup!![4] = wv4
    }

    fun setData(dataGroup: Array<List<String>?>, visibleCount: Int) {
        if (dataGroup.size > 5) {
            throw IllegalArgumentException("数据异常,请不要超过5组数据")
        }
        wheelGroup?.forEach {
            it?.visibility = View.GONE
        }
        this.visibleCount = visibleCount
        selectedDatas = arrayOfNulls(dataGroup.size)
        for (i in 0 until dataGroup.size) {
            wheelGroup!![i]!!.visibility = View.VISIBLE
            wheelGroup!![i]!!.setItems(dataGroup[i])
            wheelGroup!![i]!!.setLoop(true)
            wheelGroup!![i]!!.currentItem = defaultSelected
            wheelGroup!![i]!!.setVisibleItemCount(visibleCount + 2)
            selectedDatas!![i] = dataGroup[i]!![defaultSelected]
            wheelGroup!![i]!!.setOnItemSelectedListener { index -> selectedDatas!![i] = dataGroup[i]!![index] }
        }
    }

    fun setData(groupNum: Int, dataGroup: Array<List<String>?>) {
        if (groupNum != dataGroup.size) {
            throw IllegalArgumentException("参数异常")
        }

        wheelGroup = arrayOfNulls(groupNum)
        this.dataGroup = dataGroup
        for (i in 0 until groupNum) {
            val wheelView = createWheelView(dataGroup[i])
            addView(wheelView)
            wheelGroup!![i] = wheelView
        }
        invalidate()
    }

    private fun createWheelView(dataList: List<String>?): WheelView {
        val wheelView = WheelView(ctx)
        val lp = LayoutParams(0, LayoutParams.WRAP_CONTENT)
        lp.weight = 1f
        wheelView.layoutParams = lp
        wheelView.setItems(dataList)
        wheelView.currentItem = 0
        wheelView.setVisibleItemCount(visibleCount + 2)
        return wheelView
    }

}