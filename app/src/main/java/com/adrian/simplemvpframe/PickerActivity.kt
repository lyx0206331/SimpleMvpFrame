package com.adrian.simplemvpframe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.adrian.pickerlib.AddressPicker
import com.adrian.pickerlib.CustomPicker
import com.adrian.pickerlib.DatePicker
import com.adrian.pickerlib.wheelview.WheelView

class PickerActivity : AppCompatActivity() {

    private var btnAddress: Button? = null
    private var btnDate: Button? = null
//    private var customPicker: CustomPicker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        btnAddress = findViewById(R.id.btn_address)
        btnDate = findViewById(R.id.btn_date)

        btnAddress!!.setOnClickListener {
            val picker = AddressPicker(this)

            picker.setAddressListener { province, city, area -> btnAddress!!.text = "$province-$city-$area" }

            picker.show()
        }

        btnDate!!.setOnClickListener {
            val picker = DatePicker(this)

            picker.setDateListener { year, month, day -> btnDate!!.text = "$year-$month-$day" }

            picker.show()
        }

//        customPicker = findViewById(R.id.customPicker)
        val datas: Array<List<String>?> = arrayOfNulls(3)
        datas[0] = arrayListOf("赵", "钱", "孙", "李", "周", "吴", "郑", "王")
        datas[1] = arrayListOf("a", "b", "c", "d", "e")
        datas[2] = arrayListOf("1", "2", "3", "4")
//        customPicker!!.setData(3, datas)
        val wv0: WheelView = findViewById(R.id.wv_0)
        val wv1: WheelView = findViewById(R.id.wv_1)
        val wv2: WheelView = findViewById(R.id.wv_2)

        wv0.setItems(datas[0])
        wv1.setItems(datas[1])
        wv2.setItems(datas[2])

        wv0.setVisibleItemCount(3)
        wv1.setVisibleItemCount(3)
        wv2.setVisibleItemCount(3)

        wv0.currentItem = 0
        wv1.currentItem = 1
        wv2.currentItem = 2
    }
}
