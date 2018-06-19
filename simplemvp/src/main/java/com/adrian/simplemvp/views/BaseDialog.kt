package com.adrian.simplemvp.views

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.adrian.simplemvp.R

/**
 * author:RanQing
 * date:2018/6/19
 * description:
 **/
abstract class BaseDialog(context: Context) : Dialog(context, R.style.Dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(getLayoutResId())
        initViews()
    }

    abstract fun getLayoutResId(): Int
    abstract fun initViews()
}