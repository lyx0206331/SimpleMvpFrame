package com.adrian.simplemvpframe

import android.graphics.Typeface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.adrian.pickerlib.*
import com.adrian.pickerlib.CustomWheelGroup.OnDataGroupChangeListener
import com.adrian.pickerlib.wheelview.WheelView
import kotlinx.android.synthetic.main.activity_picker.*
import org.jetbrains.anko.toast

class PickerActivity : AppCompatActivity() {

    private var btnInit: Button? = null
    private var btnHasValue: Button? = null
    private var btnHasBakValue: Button? = null
    private var btnNoValue: Button? = null
    private var btnDate: Button? = null
    private var customPicker: CustomPicker? = null
    private var btnShowData: Button? = null
    private var wheelViewGroup: CustomWheelGroup? = null
    private var switchablePicker2: SwitchablePicker2? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        testSwitcherPicker2()

        btnDate = findViewById(R.id.btn_date)
        btnDate!!.setOnClickListener {
            val picker = DatePicker(this)

            picker.setDateListener { year, month, day -> btnDate!!.text = "$year-$month-$day" }

            picker.show()
        }

        btnShowData = findViewById(R.id.btn_showData)
        btnShowData!!.setOnClickListener {
            val result = customPicker!!.selectedDatas
            var resultStr = StringBuilder()
            result!!.forEach { resultStr.append(it) }
            btnShowData!!.text = resultStr
            toast(resultStr)
        }

        customPicker = findViewById(R.id.customPicker)
        val datas: Array<List<String>?> = arrayOfNulls(3)
        datas[0] = arrayListOf("赵", "钱", "孙", "李", "周", "吴", "郑", "王")
        datas[1] = arrayListOf("a", "b", "c", "d", "e")
        datas[2] = arrayListOf("1", "2", "3", "4")
//        datas[3] = arrayListOf("1000", "20000", "300", "adsf")
        customPicker!!.setData(datas, 5, "单位")
//        val wv0: WheelView = findViewById(R.id.wv_0)
//        val wv1: WheelView = findViewById(R.id.wv_1)
//        val wv2: WheelView = findViewById(R.id.wv_2)
//
//        wv0.setItems(datas[0])
//        wv1.setItems(datas[1])
//        wv2.setItems(datas[2])
//
//        wv0.setVisibleItemCount(5)
//        wv1.setVisibleItemCount(5)
//        wv2.setVisibleItemCount(5)
//
//        wv0.currentItem = 0
//        wv1.currentItem = 1
//        wv2.currentItem = 2

        val data0 = arrayListOf("赵", "钱", "孙", "李", "周", "吴", "郑", "王")
        val data1 = arrayListOf("a", "b", "c", "d", "e")
        val data2 = arrayListOf("1", "2", "3", "4")
        val data3 = arrayListOf("1km", "2km", "3km", "4km", "5km", "10km")
        val data4 = arrayListOf("1kj", "5kj", "10kj", "1000kj")
        val datas2 = arrayListOf(data0, /*data1, data2,*/ data3, data4)
        wheelViewGroup = findViewById(R.id.cwg)
        wheelViewGroup?.setData(datas2, 5, "单位")
        wheelViewGroup?.isRecyclable = true
        wheelViewGroup?.dataChangedListener = object : OnDataGroupChangeListener {
            override fun onChanged(changedDataBean: ArrayList<CustomWheelGroup.ChangedDataBean>) {
                val sb = StringBuilder()
                changedDataBean.forEach { sb.append(it.data) }
                btnShowData?.text = sb
            }

        }
    }

    private fun testSwitcherPicker2() {
        btnInit = findViewById(R.id.btn_init)
        btnHasValue = findViewById(R.id.btn_hasValue)
        btnHasBakValue = findViewById(R.id.btn_hasBakValue)
        btnNoValue = findViewById(R.id.btn_NoValue)
        switchablePicker2 = findViewById(R.id.sp2)
        switchablePicker2?.setSwitcherBtnText(btnInit!!, "single", "multiple")

        val data0 = arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        val data1 = arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        val data2 = arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        val data3 = arrayListOf("1km", "2km", "3km", "4km", "5km", "10km")
        val data4 = arrayListOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        val datas = arrayListOf(data0, data1, data2, data4)

        switchablePicker2?.setData(data3, datas, 5, 3, "unit1", "unit2")

        switchablePicker2?.onDataChangedListener = object : SwitchablePicker2.OnDataGroupChangeListener {
            override fun onChanged(changedDataBean: ArrayList<SwitchablePicker2.ChangedDataBean>) {
                val sb = StringBuilder()
                changedDataBean.forEach {
                    sb.append(it.data)
                }
                Toast.makeText(this@PickerActivity, sb, Toast.LENGTH_SHORT).show()
            }

        }

        btnHasValue?.setOnClickListener {
            switchablePicker2?.setWheelPosByValue("10km", null)
        }
        btnHasBakValue?.setOnClickListener {
            switchablePicker2?.setWheelPosByValue("b35km", "379")
        }
        btnNoValue?.setOnClickListener {
            switchablePicker2?.setWheelPosByValue("asdf", "asdf")
        }
    }
}
