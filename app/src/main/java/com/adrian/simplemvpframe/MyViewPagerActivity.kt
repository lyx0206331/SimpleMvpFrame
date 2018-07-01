package com.adrian.simplemvpframe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.adrian.simplemvp.base.BaseActivityKt
import com.adrian.simplemvpframe.views.chart_view.SingleColChartView
import com.example.viewpagerlib.MyViewPager
import org.jetbrains.anko.find

class MyViewPagerActivity : BaseActivityKt() {

    private var viewPager: MyViewPager? = null

    override fun getContentLayoutId(): Int {
        return R.layout.activity_my_view_pager
    }

    override fun initView() {
        viewPager = find(R.id.my_viewPager)
    }

    override fun loadData() {
        val viewList = arrayOf(createChildView(), createChildView(), createChildView(), createChildView())
        viewPager!!.viewArray = viewList
    }

    private fun createChildView(): View {
        val childView = LayoutInflater.from(this).inflate(R.layout.item_viewpager, null, false)
        val chartView: SingleColChartView = childView.find(R.id.sccv)

        val xLabel = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31")
        val yLabel = arrayOf("0", "100", "200", "300", "400", "500", "600", "700", "800", "900")
        val data1 = intArrayOf(300, 500, 550, 500, 70, 700, 800, 750, 550, 600, 327, 300, 400, 600, 500, 700, 120, 500, 550, 900, 300, 700, 800, 750, 550, 600, 400, 300, 400, 600, 500)
        chartView.setData(data1, xLabel, yLabel)

        return childView
    }
}
