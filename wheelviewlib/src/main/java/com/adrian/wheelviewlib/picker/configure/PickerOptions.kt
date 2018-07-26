package com.adrian.wheelviewlib.picker.configure

import android.content.Context
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import com.adrian.wheelviewlib.R
import com.adrian.wheelviewlib.picker.listener.*
import com.adrian.wheelviewlib.view.WheelView
import java.util.*


/**
 * date:2018/7/26
 * author：RanQing
 * description：
 */
class PickerOptions {
    //常量
    companion object {
        private const val PICKER_VIEW_BTN_COLOR_NORMAL = 0xFF057dff
        private const val PICKER_VIEW_BG_COLOR_TITLE = 0xFFf5f5f5
        private const val PICKER_VIEW_COLOR_TITLE = 0xFF000000
        private const val PICKER_VIEW_BG_COLOR_DEFAULT = 0xFFFFFFFF

        const val TYPE_PICKER_OPTIONS = 1
        const val TYPE_PICKER_TIME = 2

    }

    var optionsSelectListener: OnOptionsSelectListener? = null
    var timeSelectListener: OnTimeSelectListener? = null

    var timeSelectChangeListener: OnTimeSelectChangeListener? = null
    var optionsSelectChangeListener: OnOptionsSelectChangeListener? = null

    var customListener: CustomListener? = null


    //options picker
    //单位字符
    var label1: String? = null
    var label2: String? = null
    var label3: String? = null
    //默认选中项
    var option1: Int = 0
    var option2: Int = 0
    var option3: Int = 0
    //x轴偏移量
    var x_offset_one: Int = 0
    var x_offset_two: Int = 0
    var x_offset_three: Int = 0

    var cyclic1 = false//是否循环，默认否
    var cyclic2 = false
    var cyclic3 = false

    var isRestoreItem = false //切换时，还原第一项


    //time picker
    var type = booleanArrayOf(true, true, true, false, false, false)//显示类型，默认显示： 年月日

    var date: Calendar? = null//当前选中时间
    var startDate: Calendar? = null//开始时间
    var endDate: Calendar? = null//终止时间
    var startYear: Int = 0//开始年份
    var endYear: Int = 0//结尾年份

    var cyclic = false//是否循环
    var isLunarCalendar = false//是否显示农历

    //单位
    var label_year: String? = null
    var label_month: String? = null
    var label_day: String? = null
    var label_hours: String? = null
    var label_minutes: String? = null
    var label_seconds: String? = null
    //单位
    var x_offset_year: Int = 0
    var x_offset_month: Int = 0
    var x_offset_day: Int = 0
    var x_offset_hours: Int = 0
    var x_offset_minutes: Int = 0
    var x_offset_seconds: Int = 0


    constructor(buildType: Int) {
        layoutRes = if (buildType == TYPE_PICKER_OPTIONS) {
            R.layout.pickerview_options
        } else {
            R.layout.pickerview_time
        }
    }

    //******* 公有字段  ******//
    var layoutRes: Int = 0
    var decorView: ViewGroup? = null
    var textGravity = Gravity.CENTER
    var context: Context? = null

    var textContentConfirm: String? = null//确定按钮文字
    var textContentCancel: String? = null//取消按钮文字
    var textContentTitle: String? = null//标题文字

    var textColorConfirm = PICKER_VIEW_BTN_COLOR_NORMAL//确定按钮颜色
    var textColorCancel = PICKER_VIEW_BTN_COLOR_NORMAL//取消按钮颜色
    var textColorTitle = PICKER_VIEW_COLOR_TITLE//标题颜色

    var bgColorWheel = PICKER_VIEW_BG_COLOR_DEFAULT//滚轮背景颜色
    var bgColorTitle = PICKER_VIEW_BG_COLOR_TITLE//标题背景颜色

    var textSizeSubmitCancel = 17//确定取消按钮大小
    var textSizeTitle = 18//标题文字大小
    var textSizeContent = 18//内容文字大小

    var textColorOut: Long = 0xFFa8a8a8 //分割线以外的文字颜色
    var textColorCenter: Long = 0xFF2a2a2a //分割线之间的文字颜色
    var dividerColor: Long = 0xFFd5d5d5 //分割线的颜色
    var backgroundId = -1 //显示时的外部背景色颜色,默认是灰色

    var lineSpacingMultiplier = 1.6f // 条目间距倍数 默认1.6
    var isDialog: Boolean = false//是否是对话框模式

    var cancelable = true//是否能取消
    var isCenterLabel = false//是否只显示中间的label,默认每个item都显示
    var font = Typeface.MONOSPACE//字体样式
    var dividerType = WheelView.DividerType.FILL//分隔线类型
}