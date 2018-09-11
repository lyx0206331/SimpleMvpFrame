package com.adrian.heartanimsample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.lv1, R.id.lv2, R.id.lv3 -> {
                if (lv1.isChecked) {
                    lv1.unLike()
                    lv2.unLike()
                    lv3.unLike()
                } else {
                    lv1.like()
                    lv2.like()
                    lv3.like()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lv1.setOnClickListener(this)
        lv2.setOnClickListener(this)
        lv3.setOnClickListener(this)
    }
}
