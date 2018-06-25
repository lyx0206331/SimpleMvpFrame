package com.adrian.wheelviewpicker.picker

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView

import com.adrian.wheelviewpicker.R
import com.adrian.wheelviewpicker.view.OnItemSelectedListener
import com.adrian.wheelviewpicker.view.WheelView

import java.util.ArrayList
import java.util.Calendar

class DatePicker(context: Context) : Dialog(context), View.OnClickListener {
    private val view: View
    private var mYearWheelView: WheelView? = null
    private var mMonthWheelView: WheelView? = null
    private var mDayWheelView: WheelView? = null
    private var mTvConfirm: TextView? = null
    private var mTvCancel: TextView? = null
    private var mOnDateCListener: OnDateCListener? = null

    private val years = ArrayList<String>()
    private val months = ArrayList<String>()
    private val days = ArrayList<String>()

    private var yearPos: Int = 0
    private var monthPos: Int = 0
    private var dayPos: Int = 0

    init {

        view = View.inflate(context, R.layout.layout_date_picker, null)

        initView()
        initData()
        setListener()

        this.setContentView(view)

        this.setCanceledOnTouchOutside(true)

        //从底部弹出
        val window = this.window
        window!!.setGravity(Gravity.BOTTOM)  //此处可以设置dialog显示的位置
        //        window.setWindowAnimations(R.style.pickerAnimationStyle);  //添加动画

        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        window.attributes = params
    }

    /**
     * 回调接口
     */
    interface OnDateCListener {
        fun onDateSelected(year: String, month: String, day: String)
    }

    fun setDateListener(mOnDateCListener: OnDateCListener) {
        this.mOnDateCListener = mOnDateCListener
    }

    private fun initView() {
        mYearWheelView = view.findViewById(R.id.wv_year)
        mMonthWheelView = view.findViewById(R.id.wv_month)
        mDayWheelView = view.findViewById(R.id.wv_day)
        mTvConfirm = view.findViewById(R.id.tv_confirm)
        mTvCancel = view.findViewById(R.id.tv_cancel)

        /**
         * 设置可见条目数量
         * 注：因为WheelView是圆形，最上面和最下面刚好在圆顶和圆底，
         * 所以最上面和最下面两个看不到，因此可见数量要比设置的少2个
         */
        mYearWheelView!!.visibleItemCount = 9
        mMonthWheelView!!.visibleItemCount = 9
        mDayWheelView!!.visibleItemCount = 9

        mYearWheelView!!.label = "年"
        mMonthWheelView!!.label = "月"
        mDayWheelView!!.label = "日"

        //        mYearWheelView.isCenterLabel(true);
        //        mMonthWheelView.isCenterLabel(true);
        //        mDayWheelView.isCenterLabel(true);
    }

    private fun setListener() {
        //年份*******************
        mYearWheelView!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                yearPos = index
                //设置日
                setDay()
            }
        }
        //月份*******************
        mMonthWheelView!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                monthPos = index
                //设置日
                setDay()
            }
        }
        //日********************
        mDayWheelView!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                dayPos = index
            }
        }

        mTvConfirm!!.setOnClickListener(this)
        mTvCancel!!.setOnClickListener(this)
    }

    private fun initData() {

        val nowCalendar = Calendar.getInstance()
        yearPos = nowCalendar.get(Calendar.YEAR) - MIN_YEAR
        monthPos = nowCalendar.get(Calendar.MONTH)
        dayPos = nowCalendar.get(Calendar.DAY_OF_MONTH) - 1

        //初始化年
        for (i in 0..MAX_YEAR - MIN_YEAR) {
            years.add(format(MIN_YEAR + i))
        }
        mYearWheelView!!.items = years
        mYearWheelView!!.setCurrentItem(yearPos)
        //初始化月
        for (i in 0..11) {
            months.add(format(i + 1))
        }
        mMonthWheelView!!.items = months
        mMonthWheelView!!.setCurrentItem(monthPos)

        //设置日
        setDay()
    }

    /**
     * 设置日
     */
    private fun setDay() {
        val isRun = isRunNian(Integer.parseInt(years[yearPos]))
        var dayCount = 0
        when (Integer.parseInt(months[monthPos])) {
            1, 3, 5, 7, 8, 10, 12 -> dayCount = 31
            2 -> if (isRun) {
                dayCount = 29
            } else {
                dayCount = 28
            }
            4, 6, 9, 11 -> dayCount = 30
        }
        days.clear()
        for (i in 0 until dayCount) {
            days.add(format(i + 1))
        }

        mDayWheelView!!.items = days
        dayPos = if (dayPos >= days.size - 1) days.size - 1 else dayPos
        mDayWheelView!!.setCurrentItem(dayPos)
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.tv_confirm) {
            if (mOnDateCListener != null) {
                var currentItem = mDayWheelView!!.selectedItem
                currentItem = if (currentItem >= days.size - 1) days.size - 1 else currentItem
                mOnDateCListener!!.onDateSelected(years[mYearWheelView!!.selectedItem], months[mMonthWheelView!!.selectedItem], days[currentItem])
            }
        }
        cancel()
    }

    /**
     * 判断是否是闰年
     *
     * @param year
     * @return
     */
    private fun isRunNian(year: Int): Boolean {
        return if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            true
        } else {
            false
        }
    }

    private fun format(num: Int): String {

        return if (num < 10) "0$num" else num.toString()
    }

    companion object {

        private val MIN_YEAR = 1900
        private val MAX_YEAR = 2200
    }
}
