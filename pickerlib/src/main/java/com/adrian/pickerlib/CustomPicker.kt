package com.adrian.pickerlib

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
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
        set(value) {
            field = value
            wheelGroup?.forEach {
                it?.setTextColorCenter(value)
            }
        }
    var txtSelectedSize = 24f
        set(value) {
            field = value
            wheelGroup?.forEach {
                it?.setSelectedTextSize(value)
            }
        }
    var txtUnselectedColor = Color.GRAY
        set(value) {
            field = value
            wheelGroup?.forEach {
                it?.setTextColorOut(value)
            }
        }
    var txtUnselectedSize = 10f
        set(value) {
            field = value
            wheelGroup?.forEach {
                it?.setUnselectedTextSize(value)
            }
        }
    var dividerColor = Color.GRAY
        set(value) {
            field = value
            wheelGroup?.forEach {
                it?.setDividerColor(value)
            }
        }
    var txtLabel: String = "步"
    var txtLabelSize = 10f
        set(value) {
            field = value
            wheelGroup?.forEach {
                it?.setTextLabelSize(value)
            }
        }
    var txtLabelColor: Int = Color.BLACK
        set(value) {
            field = value
            wheelGroup?.forEach {
                it?.setTextLabelColor(value)
            }
        }
    var visibleCount = 3
    var defaultSelected: Int = 0

    private var wheelGroup: Array<WheelView?>? = arrayOfNulls(5)

    private var dataGroup: Array<List<String>?>? = null

    var selectedDatas: Array<String?>? = null

    private var wv0: WheelView? = null
    private var wv1: WheelView? = null
    private var wv2: WheelView? = null
    private var wv3: WheelView? = null
    private var wv4: WheelView? = null
    private var tvUnit: TextView? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_custom_picker, this)

        wv0 = findViewById(R.id.wv_0)
//        wv0!!.setSelectedTextSize(txtSelectedSize)
        wv1 = findViewById(R.id.wv_1)
        wv2 = findViewById(R.id.wv_2)
        wv3 = findViewById(R.id.wv_3)
        wv4 = findViewById(R.id.wv_4)
        tvUnit = findViewById(R.id.tv_unit)

        wheelGroup!![0] = wv0
        wheelGroup!![1] = wv1
        wheelGroup!![2] = wv2
        wheelGroup!![3] = wv3
        wheelGroup!![4] = wv4
    }

    fun setData(dataGroup: Array<List<String>?>, visibleCount: Int, unit: String) {
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
        tvUnit?.text = unit
    }

}