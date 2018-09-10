package com.adrian.simplemvpframe

import android.support.v4.widget.NestedScrollView
import android.util.Log
import com.adrian.simplemvp.base.BaseActivityKt
import com.adrian.simplemvpframe.views.MyNestedScrollView
import kotlinx.android.synthetic.main.activity_test_nestedscrollview.*

/**
 * date:2018/9/10 17:18
 * author：RanQing
 * description：
 */
class TestNestedScrollViewActivity : BaseActivityKt() {
    override fun getContentLayoutId(): Int {
        return R.layout.activity_test_nestedscrollview
    }

    override fun initView() {
        myNestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            //            Log.e("TestNestedScrollView", "scrollY: $scrollY oldScrollY: $oldScrollY")
        })
        myNestedScrollView.velocityChangedListener = object : MyNestedScrollView.IScrollVelocityChangedListener {
            override fun onFlingVelocityChanged(velocityX: Float, velocityY: Float) {

            }

            override fun onPressUpVelocityChanged(velocityX: Float, velocityY: Float) {
//                Log.e("VELOCITY", "velocityX:$velocityX == velocityY:$velocityY")
            }
        }
    }

    override fun loadData() {

    }
}