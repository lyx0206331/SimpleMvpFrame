package com.adrian.pickerlib

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.adrian.pickerlib.wheelview.WheelView
import org.jetbrains.anko.backgroundColor

/**
 * date:2018/8/15 10:13
 * author：RanQing
 * description：
 */
class CustomWheelGroup : RelativeLayout {

    var bgColor: Int = Color.GRAY
        set(value) {
            field = value
            backgroundColor = value
        }
    var txtSelectedColor = Color.BLACK
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setTextColorCenter(value)
            }
        }
    var txtSelectedSize = 24f
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setSelectedTextSize(value)
            }
        }
    var txtUnselectedColor = Color.GRAY
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setTextColorOut(value)
            }
        }
    var txtUnselectedSize = 10f
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setUnselectedTextSize(value)
            }
        }
    var dividerColor = Color.GRAY
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setDividerColor(value)
            }
        }
    var unit: String = "步"
        set(value) {
            field = value
            tvUnit.text = value
        }
    var unitSize: Float = 11f
        set(value) {
            field = value
            tvUnit.textSize = field
        }
    var txtLabelSize = 10f
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setTextLabelSize(value)
            }
        }
    var txtLabelColor: Int = Color.BLACK
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setTextLabelColor(value)
            }
        }
    var visibleCount = 3
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setVisibleItemCount(value + 2)
            }
        }
    var defaultSelected: Int = 0
        set(value) {
            field = value
            wheelGroup.forEach {
                it.currentItem = value
            }
            for ((i, v) in dataGroup.withIndex()) {
                selectedDatas.add(ChangedDataBean(i, value, v[value]))
            }
            invalidate()
        }
    var isRecyclable: Boolean = false
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setLoop(value)
            }
        }

    private var dataGroup: ArrayList<ArrayList<String>> = arrayListOf()

    var selectedDatas: ArrayList<ChangedDataBean> = arrayListOf()

    var dataChangedListener: OnDataGroupChangeListener? = null

    private var wheelGroup: ArrayList<WheelView> = arrayListOf()
    private var tvUnit: TextView

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_wheel_group, this)

        wheelGroup.add(findViewById(R.id.wv_0))
        wheelGroup.add(findViewById(R.id.wv_1))
        wheelGroup.add(findViewById(R.id.wv_2))
        wheelGroup.add(findViewById(R.id.wv_3))
        wheelGroup.add(findViewById(R.id.wv_4))

        tvUnit = findViewById(R.id.tv_unit)

        backgroundColor = bgColor
    }

    fun setWheelPosByValue(valueArray: ArrayList<String>?) {
        var visibleViews: ArrayList<WheelView> = ArrayList()
        wheelGroup.forEach {
            if (it.visibility == View.VISIBLE) {
                visibleViews.add(it)
            }
        }
        if (valueArray != null && valueArray.size > visibleViews.size) {
            throw IllegalArgumentException("参数长度过长")
        }
        val wvLen = visibleViews.size
        val valueSize = valueArray?.size ?: 0
        for ((i, value) in visibleViews.withIndex()) {
            if (valueArray != null && wvLen - i - 1 < valueSize) {
                val item = valueArray[valueSize - (wvLen - i)]
                var tmpIndex = 0
                for ((j, k) in dataGroup[i].withIndex()) {
                    if (k == item) {
                        tmpIndex = j
                        selectedDatas[i].let {
                            it.wheelViewIndex = i
                            it.dataIndex = j
                            it.data = k
                        }
                        break
                    }
                }
                value.currentItem = tmpIndex
            } else {
                value.currentItem = 0
            }
        }
    }

    fun setWheelTextSize(selectedSize: Float, unSelectedSize: Float) {
        wheelGroup?.forEach {
            it?.setSelectedTextSize(selectedSize)
            it?.setUnselectedTextSize(unSelectedSize)
//            it?.setLineSpacingMultiplier(1.5f)
        }
        invalidate()
    }

    fun setDate(dataGroup: ArrayList<ArrayList<String>>, visibleCount: Int, unit: String) {
        if (dataGroup.size > 5) {
            throw IllegalArgumentException("数据异常,请不要超过5组数据")
        } else if (dataGroup == null || dataGroup.isEmpty()) {
            return
        }
        this.dataGroup = dataGroup
        val size = dataGroup.size
        selectedDatas.clear()
        for (i in 0 until 5) {
            wheelGroup[i].let {
                if (i < size) {
                    it.visibility = View.VISIBLE
                    it.setItems(dataGroup[i])
                    it.setLoop(true)
                    it.currentItem = defaultSelected
                    it.setVisibleItemCount(visibleCount + 2)
                    selectedDatas.add(ChangedDataBean(i, defaultSelected, dataGroup[i][defaultSelected]))
                    it.setOnItemSelectedListener { index ->
                        selectedDatas[i].let {
                            it.wheelViewIndex = i
                            it.dataIndex = index
                            it.data = dataGroup[i][index]
                        }
                        dataChangedListener?.onChanged(selectedDatas)
                    }
                } else {
                    it.visibility = View.GONE
                    it.setOnItemSelectedListener(null)
                }
            }
        }
        this.unit = unit
        invalidate()
    }

    interface OnDataGroupChangeListener {
        fun onChanged(changedDataBean: ArrayList<ChangedDataBean>)
    }

    /**
     * @param wheelViewIndex 滚轮索引
     * @param dataIndex 滚轮数据索引
     * @param data 数据
     */
    inner class ChangedDataBean(var wheelViewIndex: Int = 0, var dataIndex: Int = 0, var data: String)
}