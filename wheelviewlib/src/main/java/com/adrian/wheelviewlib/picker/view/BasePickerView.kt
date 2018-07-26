package com.adrian.wheelviewlib.picker.view

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import com.adrian.wheelviewlib.picker.configure.PickerOptions
import android.view.ViewGroup
import com.adrian.wheelviewlib.picker.listener.OnDismissListener


/**
 * date:2018/7/26
 * author：RanQing
 * description：
 */
open class BasePickerView(context: Context) {
    protected var contentContainer: ViewGroup? = null
    private var rootView: ViewGroup? = null//附加View 的 根View
    private var dialogView: ViewGroup? = null//附加Dialog 的 根View

    protected var mPickerOptions: PickerOptions? = null
    private var onDismissListener: OnDismissListener? = null
    private var dismissing: Boolean = false

    private var outAnim: Animation? = null
    private var inAnim: Animation? = null
    private var isShowing: Boolean = false

    protected var animGravity = Gravity.BOTTOM

    private var mDialog: Dialog? = null
    protected var clickView: View? = null//是通过哪个View弹出的
    private var isAnim = true
}