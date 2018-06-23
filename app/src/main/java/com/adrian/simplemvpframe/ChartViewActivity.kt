package com.adrian.simplemvpframe

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout
import com.adrian.simplemvpframe.views.chart_view.CustomChartView
import com.adrian.simplemvpframe.views.chart_view.SingleColChartView

/**
 * date:2018/6/13
 * author：RanQing
 * description：
 */
class ChartViewActivity : AppCompatActivity() {

    private lateinit var customBarChart1: LinearLayout
    private lateinit var customBarChart2: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chartview)

        customBarChart1 = findViewById(R.id.customBarChart1)
        initBarChart1()
//        customBarChart2 = findViewById(R.id.customBarChart2)
//        initBarChart2()
    }

    /**
     * 初始化柱状图1数据
     */
    private fun initBarChart1() {
        val xLabel = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31")
        val yLabel = arrayOf("0", "100", "200", "300", "400", "500", "600", "700", "800", "900")
        val data1 = intArrayOf(300, 500, 550, 500, 70, 700, 800, 750, 550, 600, 327, 300, 400, 600, 500, 700, 120, 500, 550, 900, 300, 700, 800, 750, 550, 600, 400, 300, 400, 600, 500)
//        val data = ArrayList<IntArray>()
//        data.add(data1)
//        val color = ArrayList<Int>()
//        color.add(R.color.color12)
//        color.add(R.color.color13)
//        color.add(R.color.color16)
//        customBarChart1.addView(CustomChartView(this, xLabel, yLabel, data, color))
        val chartView: SingleColChartView = findViewById(R.id.sccv)
        chartView.setData(data1, xLabel, yLabel)
    }

    /**
     * 初始化柱状图2数据
     */
    private fun initBarChart2() {
        val xLabel = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
        val yLabel = arrayOf("0", "100", "200", "300", "400", "500", "600", "700", "800", "900")
        val data1 = intArrayOf(300, 500, 550, 500, 300, 700, 800, 750, 550, 600, 400, 300)
        val data2 = intArrayOf(400, 600, 500, 700, 300, 500, 550, 500, 300, 700, 800, 750)
        val data = ArrayList<IntArray>()
        data.add(data1)
        data.add(data2)
        val color = ArrayList<Int>()
        color.add(R.color.color14)
        color.add(R.color.color15)
        color.add(R.color.color11)
        customBarChart2.addView(CustomChartView(this, xLabel, yLabel, data, color))
    }


}