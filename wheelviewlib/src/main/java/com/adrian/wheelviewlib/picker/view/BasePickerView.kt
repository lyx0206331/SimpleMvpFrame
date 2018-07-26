package com.adrian.wheelviewlib.picker.view

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import com.adrian.wheelviewlib.picker.configure.PickerOptions
import android.view.ViewGroup
import android.widget.FrameLayout
import com.adrian.wheelviewlib.R
import com.adrian.wheelviewlib.picker.listener.OnDismissListener


/**
 * date:2018/7/26
 * author：RanQing
 * description：
 */
open class BasePickerView(private val context: Context) {
    protected var contentContainer: ViewGroup? = null
    private var rootView: ViewGroup? = null//附加View 的 根View
    private var dialogView: ViewGroup? = null//附加Dialog 的 根View

    protected var mPickerOptions: PickerOptions? = null
    var onDismissListener: OnDismissListener? = null
    private var dismissing: Boolean = false

    private var outAnim: Animation? = null
    private var inAnim: Animation? = null
    private var isShowing: Boolean = false

    protected var animGravity = Gravity.BOTTOM

    private var mDialog: Dialog? = null
    protected var clickView: View? = null//是通过哪个View弹出的
    private var isAnim = true
    var isDialog: Boolean = false

    protected fun initViews() {
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM)

        val layoutInflater = LayoutInflater.from(context)
        if (isDialog) {
            //如果是对话框模式
            dialogView = layoutInflater.inflate(R.layout.layout_basepickerview, null, false) as ViewGroup?
            //设置界面背景为透明
            dialogView?.setBackgroundColor(Color.TRANSPARENT)
            //这个是真正要加载选择器的父布局
            contentContainer = dialogView?.findViewById(R.id.content_container)
            //设置对话框，默认左右外边距30
            params.leftMargin = 30
            params.rightMargin = 30
            contentContainer?.layoutParams = params
            //创建对话框
            createDialog()
        }
    }

    fun findViewById(id: Int): View? {
        return contentContainer?.findViewById(id)
    }

    fun createDialog() {
        if (dialogView == null) {
            mDialog = Dialog(context, R.style.custom_dialog2)
            mDialog?.setCancelable(mPickerOptions!!.cancelable)
            mDialog?.setContentView(dialogView)

            val dialogWindow = mDialog?.window
            dialogWindow?.setWindowAnimations(R.style.picker_view_scale_anim)
            dialogWindow?.setGravity(Gravity.CENTER)

            mDialog?.setOnDismissListener {
                onDismissListener?.onDismiss(this@BasePickerView)
            }
        }
    }
}