package com.adrian.pickerlib

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.adrian.pickerlib.wheelview.WheelView
import org.jetbrains.annotations.NotNull

/**
 * date:2018/8/17 9:00
 * author：RanQing
 * description：单组多组可切换滚轮，多组不能超过五组
 */
class SwitchablePicker2 : RelativeLayout {

    companion object {
        const val TAG = "SwitchablePicker"
    }

    //背景色
    var bgColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            setBackgroundColor(value)
        }
    //选中文字颜色
    var txtSelectedColor = Color.BLACK
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setTextColorCenter(value)
            }
        }
    //选中文字大小
    var txtSelectedSize = 24f
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setSelectedTextSize(value)
            }
        }
    //未选中文字颜色
    var txtUnselectedColor = Color.GRAY
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setTextColorOut(value)
            }
        }
    //未选中文字大小
    var txtUnselectedSize = 10f
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setUnselectedTextSize(value)
            }
        }
    //分隔线颜色
    var dividerColor = Color.GRAY
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setDividerColor(value)
            }
        }
    //单位
    var unit: String = "步"
        set(value) {
            field = value
            tvUnit.text = value
        }
    //单位大小
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
    //可见条目数
    var visibleCount = 3
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setVisibleItemCount(value + 2)
            }
        }
    //默认选中条目
    var defaultSelected: Int = 0
        set(value) {
            field = value
            wheelGroup.forEach {
                it.currentItem = value
            }
            for ((i, v) in multipleGroup.withIndex()) {
                selectedDatas.add(ChangedDataBean(i, value, v[value]))
            }
            invalidate()
        }
    //是否可循环
    var isRecyclable: Boolean = true
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setLoop(value)
            }
        }
    //是否单滚轮
    private var isSingleWheel: Boolean = true
        set(value) {
            field = value
            llGroup.visibility = if (value) View.GONE else View.VISIBLE
            singleWheelView.visibility = if (value) View.VISIBLE else View.GONE
        }
    //单复数滚轮切换按钮
    private var switcherView: TextView? = null
    //单滚轮时按钮文字
    private var singleTxt: String = ""
    //多滚轮时按钮文字
    private var multipleTxt: String = ""
    //所有数据组
    private var multipleGroup: ArrayList<ArrayList<String>> = arrayListOf()
    private var singleGroup: ArrayList<String> = arrayListOf()
    //已选中多滚轮数据组
    private var selectedDatas: ArrayList<ChangedDataBean> = arrayListOf()
    //已选中单滚轮数据
    private var selectedSingleData: ChangedDataBean = ChangedDataBean()
    //滚动数据变化监听
    var onDataChangedListener: OnDataGroupChangeListener? = null

    private var llGroup: LinearLayout
    private var wheelGroup: ArrayList<WheelView> = arrayListOf()
    private var singleWheelView: WheelView
    private var tvUnit: TextView

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_switchable_picker, this)

        llGroup = findViewById(R.id.llGroup)

        wheelGroup.add(findViewById(R.id.wv_0))
        wheelGroup.add(findViewById(R.id.wv_1))
        wheelGroup.add(findViewById(R.id.wv_2))
        wheelGroup.add(findViewById(R.id.wv_3))
        wheelGroup.add(findViewById(R.id.wv_4))

        singleWheelView = findViewById(R.id.single_wv)

        tvUnit = findViewById(R.id.tv_unit)

        setBackgroundColor(bgColor)
    }

    fun setWheelPosByValue(value: String) {
        if (TextUtils.isEmpty(value)) {
            isSingleWheel = true
            refreshUI()
            singleWheelView.currentItem = 0
            wheelGroup.forEach {
                it.currentItem = 0
            }
            for ((i, v) in selectedDatas.withIndex()) {
                v.dataIndex = 0
                v.data = multipleGroup[i][0]
            }
            selectedSingleData = ChangedDataBean(0, 0, singleGroup[0])
            return
        }
        if (isSingleWheel && multipleGroup[0].contains(value)) {
            for ((i, v) in multipleGroup[0].withIndex()) {
                if (value == v) {
                    selectedDatas[0].let {
                        it.wheelViewIndex = 0
                        it.data = v
                        it.dataIndex = i
                    }
                    break
                }
            }

        } else {
            val data = arrayListOf<String>()
            for (i in 0 until value!!.length) {
                data.add(value[i].toString())
            }
            setGroupPosByValue(data)
        }
    }

    fun setGroupPosByValue(valueArray: ArrayList<String>?) {
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
                for ((j, k) in multipleGroup[i].withIndex()) {
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

    /**
     * 根据已有数据还原滚轮位置
     * @param valueArray 已存在数据。为空时，数据置为滚轮索引为0的数据
     */
    fun setWheelPosByValue(valueArray: ArrayList<String>?) {
        try {
            var visibleViews: ArrayList<WheelView> = ArrayList()
            if (isSingleWheel) {
                visibleViews.add(singleWheelView)
            } else {
                wheelGroup.forEach {
                    if (it.visibility == View.VISIBLE) {
                        visibleViews.add(it)
                    }
                }
            }
            if (valueArray != null && valueArray.size > visibleViews.size) {
                throw IllegalArgumentException("参数长度过长")
            }
            val wvLen = visibleViews.size
            val valueSize = valueArray?.size ?: 0
            for ((i, value) in visibleViews.withIndex()) {
                if (valueArray != null && wvLen - i - 1 < valueSize) {  //数据索引与滚轮排列顺序相反，所以最右边的滚轮，对应最低位的数据
                    val item = valueArray[valueSize - (wvLen - i)]
                    var tmpIndex = 0
                    for ((j, k) in multipleGroup[i].withIndex()) {
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
        } catch (e: IllegalArgumentException) {
            logE(e.message)
        } catch (e: Exception) {
            logE(e.message)
        }
    }

    /**
     * 设置滚轮文字属性
     * @param selectedSize 选中文字大小
     * @param unSelectedSize 未选中文字大小
     */
    fun setWheelTextSize(selectedSize: Float, unSelectedSize: Float) {
        wheelGroup?.forEach {
            it?.setSelectedTextSize(selectedSize)
            it?.setUnselectedTextSize(unSelectedSize)
            it?.setLineSpacingMultiplier(2.0f)
        }
        singleWheelView.setSelectedTextSize(selectedSize)
        singleWheelView.setUnselectedTextSize(unSelectedSize)
        invalidate()
    }

    /**
     * 获取结果数据
     */
    fun getDataResult(): ArrayList<ChangedDataBean> {
        return if (isSingleWheel) arrayListOf(selectedSingleData) else selectedDatas
    }

    /**
     * 设置切换按键时刷新UI
     */
    fun <t : TextView> setSwitcherBtnText(@NotNull switcherView: t, @NotNull singleTxt: String, @NotNull multipleTxt: String) {
        this.switcherView = switcherView
        this.singleTxt = singleTxt
        this.multipleTxt = multipleTxt
        switcherView.setOnClickListener {
            isSingleWheel = !isSingleWheel
            refreshUI()
        }
        refreshUI()
    }

    /**
     * 刷新UI
     */
    private fun refreshUI() {
        switcherView?.text = if (isSingleWheel) singleTxt else multipleTxt
        singleWheelView.visibility = if (isSingleWheel) View.VISIBLE else View.GONE
        llGroup.visibility = if (isSingleWheel) View.GONE else View.VISIBLE
    }

    /**
     * 设置数据源
     * @param singleGroup 单滚轮数据源
     * @param multipleGroup 多滚轮数据源。每个滚轮包含一组数据
     * @param visibleCount 可见条目数
     * @param unit 单位
     */
    fun setData(@NotNull singleGroup: ArrayList<String>, @NotNull multipleGroup: ArrayList<ArrayList<String>>, visibleCount: Int, unit: String) {
        try {
            if (multipleGroup.size > 5) {
                throw IllegalArgumentException("数据异常,多组数据请不要超过5组")
            } else if (multipleGroup.isEmpty() || singleGroup.isEmpty()) {
                return
            }
            this.singleGroup = singleGroup
            this.multipleGroup = multipleGroup
            val size = multipleGroup.size
            selectedDatas.clear()
            for (i in 0 until 5) {
                wheelGroup[i].let {
                    if (i < size) {
                        it.visibility = View.VISIBLE
                        it.setItems(multipleGroup[i])
                        it.setLoop(isRecyclable)
                        it.currentItem = defaultSelected
                        it.setVisibleItemCount(visibleCount + 2)
                        selectedDatas.add(ChangedDataBean(i, defaultSelected, multipleGroup[i][defaultSelected]))
                        it.setOnItemSelectedListener { index ->
                            selectedDatas[i].let {
                                it.wheelViewIndex = i
                                it.dataIndex = index
                                it.data = multipleGroup[i][index]
                            }
                            onDataChangedListener?.onChanged(selectedDatas)
                        }
                    } else {
                        it.visibility = View.GONE
                        it.setOnItemSelectedListener(null)
                    }
                }
            }
            singleWheelView.setItems(singleGroup)
            singleWheelView.setLoop(isRecyclable)
            singleWheelView.currentItem = defaultSelected
            singleWheelView.setVisibleItemCount(visibleCount + 2)
            selectedSingleData = ChangedDataBean(0, defaultSelected, singleGroup[defaultSelected])
            singleWheelView.setOnItemSelectedListener { index ->
                selectedSingleData.wheelViewIndex = 0
                selectedSingleData.dataIndex = index
                selectedSingleData.data = singleGroup[index]
                onDataChangedListener?.onChanged(arrayListOf(selectedSingleData))
            }
            this.unit = unit
//            invalidate()
            isSingleWheel = true
            refreshUI()
        } catch (e: IllegalArgumentException) {
            logE(e.message)
        } catch (e: Exception) {
            logE(e.message)
        }
    }

    private fun logE(msg: String?) {
        Log.e(TAG, msg ?: "unknown error")
    }

    /**
     * 数据滚动变化监听
     */
    interface OnDataGroupChangeListener {
        fun onChanged(changedDataBean: ArrayList<ChangedDataBean>)
    }

    /**
     * @param wheelViewIndex 滚轮索引
     * @param dataIndex 滚轮数据索引
     * @param data 数据
     */
    inner class ChangedDataBean(var wheelViewIndex: Int = 0, var dataIndex: Int = 0, var data: String = "")
}