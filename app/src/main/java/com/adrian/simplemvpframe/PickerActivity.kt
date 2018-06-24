package com.adrian.simplemvpframe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.adrian.simplemvpframe.views.AddressPicker
import com.adrian.simplemvpframe.views.DatePicker

class PickerActivity : AppCompatActivity() {

    private var btn_address: Button? = null
    private var btn_date: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker)

        btn_address = findViewById<Button>(R.id.btn_address)
        btn_date = findViewById<Button>(R.id.btn_date)

        btn_address!!.setOnClickListener(View.OnClickListener {
            val picker = AddressPicker(this@PickerActivity)

            picker.setAddressListener { province, city, area -> btn_address!!.setText("$province-$city-$area") }

            picker.show()
        })

        btn_date!!.setOnClickListener(View.OnClickListener {
            val picker = DatePicker(this@PickerActivity)

            picker.setDateListener { year, month, day -> btn_date!!.setText("$year-$month-$day") }
            picker.show()
        })
    }
}
