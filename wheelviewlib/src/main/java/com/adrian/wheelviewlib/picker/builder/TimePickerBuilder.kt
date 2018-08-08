package com.adrian.wheelviewlib.picker.builder

import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import com.adrian.wheelviewlib.picker.configure.PickerOptions
import com.adrian.wheelviewlib.picker.listener.CustomListener
import com.adrian.wheelviewlib.picker.listener.OnTimeSelectListener
import java.util.*
import com.adrian.wheelviewlib.picker.listener.OnTimeSelectChangeListener
import com.adrian.wheelviewlib.picker.view.TimePickerView
import com.adrian.wheelviewlib.view.WheelView


/**
 * date:2018/8/7 20:03
 * author：RanQing
 * description：
 */
class TimePickerBuilder(context: Context, listener: OnTimeSelectListener) {
    private val mPickerOptions = PickerOptions(PickerOptions.TYPE_PICKER_TIME)

    init {
        mPickerOptions.context = context
        mPickerOptions.timeSelectListener = listener
    }

    fun setGravity(gravity: Int): TimePickerBuilder {
        mPickerOptions.textGravity = gravity
        return this
    }

    /**
     * new boolean[]{true, true, true, false, false, false}
     * control the "year","month","day","hours","minutes","seconds " display or hide.
     * 分别控制“年”“月”“日”“时”“分”“秒”的显示或隐藏。
     *
     * @param types 布尔型数组，长度需要设置为6。
     * @return TimePickerBuilder
     */
    fun setType(types: BooleanArray): TimePickerBuilder {
        mPickerOptions.type = types
        return this
    }

    fun isDialog(isDialog: Boolean): TimePickerBuilder {
        mPickerOptions.isDialog = isDialog
        return this
    }

    fun setSubmitText(confirmText: String): TimePickerBuilder {
        mPickerOptions.textContentConfirm = confirmText
        return this
    }

    fun setCancelText(cancelText: String): TimePickerBuilder {
        mPickerOptions.textContentCancel = cancelText
        return this
    }

    fun setTitleText(titleText: String): TimePickerBuilder {
        mPickerOptions.textContentTitle = titleText
        return this
    }

    fun setSubmitColor(confirmColor: Int): TimePickerBuilder {
        mPickerOptions.textColorConfirm = confirmColor
        return this
    }

    fun setCancelColor(cancelColor: Int): TimePickerBuilder {
        mPickerOptions.textColorCancel = cancelColor
        return this
    }

    /**
     * ViewGroup 类型的容器
     *
     * @param decorView 选择器会被添加到此容器中
     * @return TimePickerBuilder
     */
    fun setDecorView(decorView: ViewGroup): TimePickerBuilder {
        mPickerOptions.decorView = decorView
        return this
    }

    fun setBgColor(bgColorWheel: Int): TimePickerBuilder {
        mPickerOptions.bgColorWheel = bgColorWheel
        return this
    }

    fun setTitleBgColor(titleBgColor: Int): TimePickerBuilder {
        mPickerOptions.bgColorTitle = titleBgColor
        return this
    }

    fun setTitleColor(titleColor: Int): TimePickerBuilder {
        mPickerOptions.textColorTitle = titleColor
        return this
    }

    fun setSubCancelSize(submitCancelTextSize: Int): TimePickerBuilder {
        mPickerOptions.textSizeSubmitCancel = submitCancelTextSize
        return this
    }

    fun setTitleSize(titleTextSize: Int): TimePickerBuilder {
        mPickerOptions.textSizeTitle = titleTextSize
        return this
    }

    fun setContentTextSize(contentTextSize: Int): TimePickerBuilder {
        mPickerOptions.textSizeContent = contentTextSize
        return this
    }

    /**
     * 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
     *
     * @param date
     * @return TimePickerBuilder
     */
    fun setDate(date: Calendar): TimePickerBuilder {
        mPickerOptions.date = date
        return this
    }

    fun setLayoutRes(res: Int, customListener: CustomListener): TimePickerBuilder {
        mPickerOptions.layoutRes = res
        mPickerOptions.customListener = customListener
        return this
    }

    /**
     * 设置起始时间
     * 因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
     */
    fun setRangeDate(startDate: Calendar, endDate: Calendar): TimePickerBuilder {
        mPickerOptions.startDate = startDate
        mPickerOptions.endDate = endDate
        return this
    }

    /**
     * 设置间距倍数,但是只能在1.0-4.0f之间
     *
     * @param lineSpacingMultiplier
     */
    fun setLineSpacingMultiplier(lineSpacingMultiplier: Float): TimePickerBuilder {
        mPickerOptions.lineSpacingMultiplier = lineSpacingMultiplier
        return this
    }

    /**
     * 设置分割线的颜色
     *
     * @param dividerColor
     */
    fun setDividerColor(dividerColor: Int): TimePickerBuilder {
        mPickerOptions.dividerColor = dividerColor
        return this
    }

    /**
     * 设置分割线的类型
     *
     * @param dividerType
     */
    fun setDividerType(dividerType: WheelView.DividerType): TimePickerBuilder {
        mPickerOptions.dividerType = dividerType
        return this
    }

    /**
     * //显示时的外部背景色颜色,默认是灰色
     *
     * @param backgroundId
     */

    fun setBackgroundId(backgroundId: Int): TimePickerBuilder {
        mPickerOptions.backgroundId = backgroundId
        return this
    }

    /**
     * 设置分割线之间的文字的颜色
     *
     * @param textColorCenter
     */
    fun setTextColorCenter(textColorCenter: Int): TimePickerBuilder {
        mPickerOptions.textColorCenter = textColorCenter
        return this
    }

    /**
     * 设置分割线以外文字的颜色
     *
     * @param textColorOut
     */
    fun setTextColorOut(textColorOut: Int): TimePickerBuilder {
        mPickerOptions.textColorOut = textColorOut
        return this
    }

    fun isCyclic(cyclic: Boolean): TimePickerBuilder {
        mPickerOptions.cyclic = cyclic
        return this
    }

    fun setOutSideCancelable(cancelable: Boolean): TimePickerBuilder {
        mPickerOptions.cancelable = cancelable
        return this
    }

    fun setLunarCalendar(lunarCalendar: Boolean): TimePickerBuilder {
        mPickerOptions.isLunarCalendar = lunarCalendar
        return this
    }


    fun setLabel(label_year: String, label_month: String, label_day: String, label_hours: String, label_mins: String, label_seconds: String): TimePickerBuilder {
        mPickerOptions.label_year = label_year
        mPickerOptions.label_month = label_month
        mPickerOptions.label_day = label_day
        mPickerOptions.label_hours = label_hours
        mPickerOptions.label_minutes = label_mins
        mPickerOptions.label_seconds = label_seconds
        return this
    }

    /**
     * 设置X轴倾斜角度[ -90 , 90°]
     *
     * @param x_offset_year    年
     * @param x_offset_month   月
     * @param x_offset_day     日
     * @param x_offset_hours   时
     * @param x_offset_minutes 分
     * @param x_offset_seconds 秒
     * @return
     */
    fun setTextXOffset(x_offset_year: Int, x_offset_month: Int, x_offset_day: Int,
                       x_offset_hours: Int, x_offset_minutes: Int, x_offset_seconds: Int): TimePickerBuilder {
        mPickerOptions.x_offset_year = x_offset_year
        mPickerOptions.x_offset_month = x_offset_month
        mPickerOptions.x_offset_day = x_offset_day
        mPickerOptions.x_offset_hours = x_offset_hours
        mPickerOptions.x_offset_minutes = x_offset_minutes
        mPickerOptions.x_offset_seconds = x_offset_seconds
        return this
    }

    fun isCenterLabel(isCenterLabel: Boolean): TimePickerBuilder {
        mPickerOptions.isCenterLabel = isCenterLabel
        return this
    }

    /**
     * @param listener 切换item项滚动停止时，实时回调监听。
     * @return
     */
    fun setTimeSelectChangeListener(listener: OnTimeSelectChangeListener): TimePickerBuilder {
        mPickerOptions.timeSelectChangeListener = listener
        return this
    }

    fun build(): TimePickerView {
        return TimePickerView(mPickerOptions)
    }
}