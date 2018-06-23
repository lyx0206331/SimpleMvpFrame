package com.adrian.simplemvpframe.views.chart_view

import android.content.Context
import android.widget.TextView
import com.adrian.simplemvp.views.BaseDialog
import com.adrian.simplemvpframe.R

/**
 * author:RanQing
 * date:2018/6/20
 * description:
 **/
class MyDialog(context: Context, private val listener: IOnClickListener) : BaseDialog(context) {

    private lateinit var tvTitle: TextView
    private lateinit var tvContent: TextView
    private lateinit var tvConfirm: TextView
    private lateinit var tvCancel: TextView

    override fun getLayoutResId(): Int {
        return R.layout.dialog_my_layout
    }

    override fun initViews() {
        tvTitle = findViewById(R.id.tv_title)
        tvContent = findViewById(R.id.tv_content)
        tvConfirm = findViewById(R.id.tv_confirm)
        tvCancel = findViewById(R.id.tv_cancel)

        tvConfirm.setOnClickListener {
            listener?.clickConfirm(this)
        }
        tvCancel.setOnClickListener {
            listener?.clickCancel(this)
            dismiss()
        }
    }

    interface IOnClickListener {
        fun clickConfirm(dialog: MyDialog)
        fun clickCancel(dialog: MyDialog)
    }
}