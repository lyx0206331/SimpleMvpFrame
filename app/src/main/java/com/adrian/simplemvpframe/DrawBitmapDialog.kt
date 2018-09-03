package com.adrian.simplemvpframe

import android.content.Context
import com.adrian.simplemvp.views.BaseDialog
import kotlinx.android.synthetic.main.layout_supercircle.*

/**
 * date:2018/9/3 14:22
 * author：RanQing
 * description：
 */
class DrawBitmapDialog(context: Context) : BaseDialog(context) {
    override fun getLayoutResId(): Int {
        return R.layout.dialog_draw_bitmap
    }

    override fun initViews() {
        tv
    }
}