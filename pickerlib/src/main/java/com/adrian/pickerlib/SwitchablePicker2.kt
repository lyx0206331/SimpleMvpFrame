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
import java.util.regex.Pattern

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
            singleWheelView.setTextColorCenter(value)
        }
    //选中文字大小
    var txtSelectedSize = 24f
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setSelectedTextSize(value)
            }
            singleWheelView.setSelectedTextSize(value)
        }
    //未选中文字颜色
    var txtUnselectedColor = Color.GRAY
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setTextColorOut(value)
            }
            singleWheelView.setTextColorOut(value)
        }
    //未选中文字大小
    var txtUnselectedSize = 10f
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setUnselectedTextSize(value)
            }
            singleWheelView.setUnselectedTextSize(value)
        }
    //分隔线颜色
    var dividerColor = Color.GRAY
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setDividerColor(value)
            }
            singleWheelView.setDividerColor(value)
        }
    private var singleUnit = ""
    private var multipleUnit = ""
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
            singleWheelView.setTextLabelSize(value)
        }
    var txtLabelColor: Int = Color.BLACK
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setTextLabelColor(value)
            }
            singleWheelView.setTextLabelColor(value)
        }
    //可见条目数
    var singleVisibleCount = 3
        set(value) {
            field = value
            singleWheelView.setVisibleItemCount(value + 2)
        }
    var multipleVisibleCount = 3
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
            singleWheelView.currentItem = value
            if (!singleGroup.isEmpty()) {
                selectedSingleData = ChangedDataBean(0, singleWheelView.currentItem, singleGroup[singleWheelView.currentItem])
            }
            for ((i, v) in multipleGroup.withIndex()) {
                wheelGroup[i].let {
                    it.currentItem = value
                    selectedDatas.add(ChangedDataBean(i, it.currentItem, v[it.currentItem]))
                }
            }
        }
    //是否可循环
    var isRecyclable: Boolean = true
        set(value) {
            field = value
            wheelGroup.forEach {
                it.setLoop(value)
            }
            singleWheelView.setLoop(value)
        }
    //是否单滚轮
    var isSingleWheel: Boolean = true
        set(value) {
            field = value
            refreshUI()
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

    /**
     * 初始化选中数据.根据传入的值调整刷新滚轮UI.执行此方法前请先初始化数据源initDataSource(...)
     * @param value 待查询值。
     * @param bakValue 备用值。当待查询值查询不到时，查询备用值.当value与bakValue单位不一致时，此参数有效
     */
    fun initSelectedValue(@NotNull value: String, bakValue: Int = 0) {
        //空值，无历史数据
        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(value.replace("0", ""))) {
            isSingleWheel = true
            singleWheelView.currentItem = 0
            selectedSingleData = ChangedDataBean(0, 0, singleGroup[0])

            wheelGroup.forEach {
                it.currentItem = 0
            }
            for ((i, v) in selectedDatas.withIndex()) {
                v.dataIndex = 0
                v.data = multipleGroup[i][0]
            }
            return
        }

        //首先查询单滚轮数据
        for ((i, v) in singleGroup.withIndex()) {
            if (v == value) {
                isSingleWheel = true
//                refreshUI()
                singleWheelView.currentItem = i
                selectedSingleData = ChangedDataBean(0, i, value)
                //单滚轮有数据时，也要保证多滚轮定位同步
                setMultipleWheelData(if (bakValue == 0) value else "$bakValue")
                return
            }
        }

        //若单滚轮中未找到传入数据，进入多滚轮查询
        isSingleWheel = false
        //备用值存在时，多滚轮先查询备用值
        setMultipleWheelData(if (bakValue == 0) value else "$bakValue")
        //当单滚轮中不存在value时，单滚轮重置为0
        singleWheelView.currentItem = 0
        selectedSingleData = ChangedDataBean(0, 0, singleGroup[0])
    }

    /**
     * 根据传入值设置滚轮
     * @param value 传入值.纯数字组成字符串
     */
    private fun setMultipleWheelData(value: String) {
        if (!isNumeric(value)) return
        val valueArray = arrayListOf<String>()
        for (i in 0 until value.length) {
            valueArray.add("${value[i]}")
        }
        var visibleViews: ArrayList<WheelView> = ArrayList()
        wheelGroup.forEach {
            if (it.visibility == View.VISIBLE) {
                visibleViews.add(it)
            }
        }
        if (valueArray.size > visibleViews.size) {
            throw IllegalArgumentException("参数长度过长")
        }
        val wvLen = visibleViews.size
        val valueSize = valueArray?.size
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
     * 利用正则表达式判断字符串是否是数字
     * @param str
     * @return
     */
    private fun isNumeric(str: String): Boolean {
        val pattern = Pattern.compile("[0-9]*")
        val isNum = pattern.matcher(str)
        return isNum.matches()
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
     * 获取当前可见滚轮选中数据
     */
    fun getDataResult(): ArrayList<ChangedDataBean> {
        return if (isSingleWheel) arrayListOf(selectedSingleData) else selectedDatas
    }

    /**
     * 获取单滚轮或多滚轮选中数据
     * @param isSingle true为单滚轮,false为多滚轮
     */
    fun getDataResult(isSingle: Boolean): ArrayList<ChangedDataBean> {
        return if (isSingle) arrayListOf(selectedSingleData) else selectedDatas
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
//            refreshUI()
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
        tvUnit.text = if (isSingleWheel) singleUnit else multipleUnit
    }

    /**
     * 初始化数据源
     * @param singleGroup 单滚轮数据源
     * @param multipleCount 多滚轮数据组数量。每个滚轮包含一组数据,分别为0-9
     * @param singleVisibleCount 单滚轮可见条目数
     * @param multipleVisibleCount 多滚轮可见条目数
     * @param singleUnit 单滚轮显示单位
     * @param multipleUnit 多滚轮显示单位
     */
    fun initDataSource(@NotNull singleGroup: ArrayList<String>, multipleCount: Int, singleVisibleCount: Int, multipleVisibleCount: Int, @NotNull singleUnit: String, @NotNull multipleUnit: String) {
        try {
            if (multipleCount > 5) {
                throw IllegalArgumentException("数据异常,多组数据请不要超过5组")
            }
            this.singleGroup = singleGroup
            this.singleUnit = singleUnit
            this.multipleUnit = multipleUnit
            val data = arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
            multipleGroup.clear()
            for (i in 0..multipleCount) {
                multipleGroup.add(data)
            }
            selectedDatas.clear()
            for (i in 0 until 5) {
                wheelGroup[i].let {
                    if (i < multipleCount) {
                        it.visibility = View.VISIBLE
                        it.setItems(multipleGroup[i])
                        it.setLoop(isRecyclable)
                        it.currentItem = defaultSelected
                        it.setVisibleItemCount(multipleVisibleCount + 2)
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
            singleWheelView.setVisibleItemCount(singleVisibleCount + 2)
            selectedSingleData = ChangedDataBean(0, defaultSelected, singleGroup[defaultSelected])
            singleWheelView.setOnItemSelectedListener { index ->
                selectedSingleData.wheelViewIndex = 0
                selectedSingleData.dataIndex = index
                selectedSingleData.data = singleGroup[index]
                onDataChangedListener?.onChanged(arrayListOf(selectedSingleData))
            }
            isSingleWheel = true
        } catch (e: IllegalArgumentException) {
            logE(e.message)
        } catch (e: Exception) {
            logE(e.message)
        }
    }

    /**
     * 初始化数据源
     * @param singleGroup 单滚轮数据源
     * @param multipleGroup 多滚轮数据源。每个滚轮包含一组数据
     * @param singleVisibleCount 单滚轮可见条目数
     * @param multipleVisibleCount 多滚轮可见条目数
     * @param singleUnit 单滚轮显示单位
     * @param multipleUnit 多滚轮显示单位
     */
    fun initDataSource(@NotNull singleGroup: ArrayList<String>, @NotNull multipleGroup: ArrayList<ArrayList<String>>, singleVisibleCount: Int, multipleVisibleCount: Int, @NotNull singleUnit: String, @NotNull multipleUnit: String) {
        try {
            if (multipleGroup.size > 5) {
                throw IllegalArgumentException("数据异常,多组数据请不要超过5组")
            } else if (multipleGroup.isEmpty() || singleGroup.isEmpty()) {
                return
            }
            this.singleGroup = singleGroup
            this.multipleGroup = multipleGroup
            this.singleUnit = singleUnit
            this.multipleUnit = multipleUnit
            val size = multipleGroup.size
            selectedDatas.clear()
            for (i in 0 until 5) {
                wheelGroup[i].let {
                    if (i < size) {
                        it.visibility = View.VISIBLE
                        it.setItems(multipleGroup[i])
                        it.setLoop(isRecyclable)
                        it.currentItem = defaultSelected
                        it.setVisibleItemCount(multipleVisibleCount + 2)
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
            singleWheelView.setVisibleItemCount(singleVisibleCount + 2)
            selectedSingleData = ChangedDataBean(0, defaultSelected, singleGroup[defaultSelected])
            singleWheelView.setOnItemSelectedListener { index ->
                selectedSingleData.wheelViewIndex = 0
                selectedSingleData.dataIndex = index
                selectedSingleData.data = singleGroup[index]
                onDataChangedListener?.onChanged(arrayListOf(selectedSingleData))
            }
            isSingleWheel = true
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