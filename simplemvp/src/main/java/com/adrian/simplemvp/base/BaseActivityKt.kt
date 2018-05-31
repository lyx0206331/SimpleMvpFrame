package com.adrian.simplemvp.base

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.adrian.simplemvp.R

abstract class BaseActivityKt : AppCompatActivity(), IBaseViewKt {

    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setCancelable(false)
    }

    override fun showLoading() {
        if (!mProgressDialog.isShowing) {
            mProgressDialog.show()
        }
    }

    override fun hideLoading() {
        if (mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
    }

    override fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun showError() {
        showToast(resources.getString(R.string.error_msg))
    }

    override fun getContext(): Context {
        return this
    }
}