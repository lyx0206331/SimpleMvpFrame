package com.adrian.simplemvpframe

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.adrian.simplemvpframe.views.wheel_view1.NumericWheelAdapter
import com.adrian.simplemvpframe.views.wheel_view1.WheelView

class WheelViewActivity : AppCompatActivity() {

    private var callback: DateListener? = null
    private var wheelView: WheelView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wheel_view)

        wheelView = findViewById(R.id.wheel_view)
        wheelView!!.adapter = NumericWheelAdapter(0, 23)
        wheelView!!.visibleItems = 3
        wheelView!!.currentItem = 0
    }

    interface DateListener {
        fun newDate(h: Int, m: Int)

    }
}
