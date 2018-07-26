package com.adrian.wheelviewlib.picker.builder

import android.content.Context
import com.adrian.wheelviewlib.picker.configure.PickerOptions
import com.adrian.wheelviewlib.picker.listener.OnOptionsSelectListener
import com.adrian.wheelviewlib.picker.listener.OnOptionsSelectChangeListener
import android.graphics.Typeface
import com.adrian.wheelviewlib.picker.listener.CustomListener
import android.view.ViewGroup
import com.adrian.wheelviewlib.picker.view.OptionsPickerView
import com.adrian.wheelviewlib.view.WheelView


/**
 * date:2018/7/26
 * author：RanQing
 * description：
 */
class OptionsPickerBuilder(context: Context, listener: OnOptionsSelectListener) {
    //配置类
    private val mPickerOptions: PickerOptions = PickerOptions(PickerOptions.TYPE_PICKER_OPTIONS)

    init {
        mPickerOptions.context = context
        mPickerOptions.optionsSelectListener = listener
    }

    fun setSubmitText(confirmTxt: String): OptionsPickerBuilder {
        mPickerOptions.textContentConfirm = confirmTxt
        return this
    }

    fun setCancelText(cancelTxt: String): OptionsPickerBuilder {
        mPickerOptions.textContentCancel = cancelTxt
        return this
    }

    fun setTitleText(titleTxt: String): OptionsPickerBuilder {
        mPickerOptions.textContentTitle = titleTxt
        return this
    }

    fun isDialog(isDialog: Boolean): OptionsPickerBuilder {
        mPickerOptions.isDialog = isDialog
        return this
    }

    fun setSubmitColor(confirmTxtColor: Long): OptionsPickerBuilder {
        mPickerOptions.textColorConfirm = confirmTxtColor
        return this
    }

    fun setCancelColor(cancelTxtColor: Long): OptionsPickerBuilder {
        mPickerOptions.textColorCancel = cancelTxtColor
        return this
    }

    /**
     * 显示时的外部背景色颜色,默认是灰色
     *
     * @param backgroundId color resId.
     * @return
     */
    fun setBackgroundId(backgroundId: Int): OptionsPickerBuilder {
        mPickerOptions.backgroundId = backgroundId
        return this
    }

    /**
     * ViewGroup 类型
     * 设置PickerView的显示容器
     *
     * @param decorView Parent View.
     * @return
     */
    fun setDecorView(decorView: ViewGroup): OptionsPickerBuilder {
        mPickerOptions.decorView = decorView
        return this
    }

    fun setLayoutRes(res: Int, listener: CustomListener): OptionsPickerBuilder {
        mPickerOptions.layoutRes = res
        mPickerOptions.customListener = listener
        return this
    }

    fun setBgColor(bgColorWheel: Long): OptionsPickerBuilder {
        mPickerOptions.bgColorWheel = bgColorWheel
        return this
    }

    fun setTitleBgColor(bgColorTitle: Long): OptionsPickerBuilder {
        mPickerOptions.bgColorTitle = bgColorTitle
        return this
    }

    fun setTitleColor(textColorTitle: Long): OptionsPickerBuilder {
        mPickerOptions.textColorTitle = textColorTitle
        return this
    }

    fun setSubCalSize(textSizeSubmitCancel: Int): OptionsPickerBuilder {
        mPickerOptions.textSizeSubmitCancel = textSizeSubmitCancel
        return this
    }

    fun setTitleSize(textSizeTitle: Int): OptionsPickerBuilder {
        mPickerOptions.textSizeTitle = textSizeTitle
        return this
    }

    fun setContentTextSize(textSizeContent: Int): OptionsPickerBuilder {
        mPickerOptions.textSizeContent = textSizeContent
        return this
    }

    fun setOutSideCancelable(cancelable: Boolean): OptionsPickerBuilder {
        mPickerOptions.cancelable = cancelable
        return this
    }


    fun setLabels(label1: String, label2: String, label3: String): OptionsPickerBuilder {
        mPickerOptions.label1 = label1
        mPickerOptions.label2 = label2
        mPickerOptions.label3 = label3
        return this
    }

    /**
     * 设置Item 的间距倍数，用于控制 Item 高度间隔
     *
     * @param lineSpacingMultiplier 浮点型，1.0-4.0f 之间有效,超过则取极值。
     */
    fun setLineSpacingMultiplier(lineSpacingMultiplier: Float): OptionsPickerBuilder {
        mPickerOptions.lineSpacingMultiplier = lineSpacingMultiplier
        return this
    }

    /**
     * Set item divider line type color.
     *
     * @param dividerColor color resId.
     */
    fun setDividerColor(dividerColor: Long): OptionsPickerBuilder {
        mPickerOptions.dividerColor = dividerColor
        return this
    }

    /**
     * Set item divider line type.
     *
     * @param dividerType enum Type [WheelView.DividerType]
     */
    fun setDividerType(dividerType: WheelView.DividerType): OptionsPickerBuilder {
        mPickerOptions.dividerType = dividerType
        return this
    }

    /**
     * Set the textColor of selected item.
     *
     * @param textColorCenter color res.
     */
    fun setTextColorCenter(textColorCenter: Long): OptionsPickerBuilder {
        mPickerOptions.textColorCenter = textColorCenter
        return this
    }

    /**
     * Set the textColor of outside item.
     *
     * @param textColorOut color resId.
     */
    fun setTextColorOut(textColorOut: Long): OptionsPickerBuilder {
        mPickerOptions.textColorOut = textColorOut
        return this
    }

    fun setTypeface(font: Typeface): OptionsPickerBuilder {
        mPickerOptions.font = font
        return this
    }

    fun setCyclic(cyclic1: Boolean, cyclic2: Boolean, cyclic3: Boolean): OptionsPickerBuilder {
        mPickerOptions.cyclic1 = cyclic1
        mPickerOptions.cyclic2 = cyclic2
        mPickerOptions.cyclic3 = cyclic3
        return this
    }

    fun setSelectOptions(option1: Int): OptionsPickerBuilder {
        mPickerOptions.option1 = option1
        return this
    }

    fun setSelectOptions(option1: Int, option2: Int): OptionsPickerBuilder {
        mPickerOptions.option1 = option1
        mPickerOptions.option2 = option2
        return this
    }

    fun setSelectOptions(option1: Int, option2: Int, option3: Int): OptionsPickerBuilder {
        mPickerOptions.option1 = option1
        mPickerOptions.option2 = option2
        mPickerOptions.option3 = option3
        return this
    }

    fun setTextXOffset(xoffset_one: Int, xoffset_two: Int, xoffset_three: Int): OptionsPickerBuilder {
        mPickerOptions.x_offset_one = xoffset_one
        mPickerOptions.x_offset_two = xoffset_two
        mPickerOptions.x_offset_three = xoffset_three
        return this
    }

    fun isCenterLabel(isCenterLabel: Boolean): OptionsPickerBuilder {
        mPickerOptions.isCenterLabel = isCenterLabel
        return this
    }

    /**
     * 切换选项时，是否还原第一项
     *
     * @param isRestoreItem true：还原； false: 保持上一个选项
     * @return TimePickerBuilder
     */
    fun isRestoreItem(isRestoreItem: Boolean): OptionsPickerBuilder {
        mPickerOptions.isRestoreItem = isRestoreItem
        return this
    }

    /**
     * @param listener 切换item项滚动停止时，实时回调监听。
     * @return
     */
    fun setOptionsSelectChangeListener(listener: OnOptionsSelectChangeListener): OptionsPickerBuilder {
        mPickerOptions.optionsSelectChangeListener = listener
        return this
    }


    fun <T> build(): OptionsPickerView<T> {
        return OptionsPickerView(mPickerOptions)
    }
}