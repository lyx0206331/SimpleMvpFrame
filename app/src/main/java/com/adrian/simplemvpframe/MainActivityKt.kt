package com.adrian.simplemvpframe

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.adrian.simplemvp.MvpPresenterKt
import com.adrian.simplemvp.MvpViewKt
import com.adrian.simplemvp.base.BaseActivityKt
import com.adrian.simplemvpframe.views.chart_view.MyDialog

class MainActivityKt : BaseActivityKt(), MvpViewKt {

    private lateinit var text: TextView
    private lateinit var presenter: MvpPresenterKt

    private var dialog: MyDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        text = findViewById(R.id.text)

        presenter = MvpPresenterKt()
        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    fun getData(view: View) {
        presenter.getData("normal")
    }

    fun getDataForFailure(view: View) {
        presenter.getData("failure")
    }

    fun getDataForError(view: View) {
        presenter.getData("error")
    }

    override fun showData(data: String) {
        text.text = data

        if (dialog == null) {
            dialog = MyDialog(this, object : MyDialog.IOnClickListener {
                override fun clickConfirm(dialog: MyDialog) {
                    Log.e("DIALOG", "确定")
                    Toast.makeText(this@MainActivityKt, "确定", Toast.LENGTH_SHORT).show()
                }

                override fun clickCancel(dialog: MyDialog) {
                    Log.e("DIALOG", "取消")
                    Toast.makeText(this@MainActivityKt, "取消", Toast.LENGTH_SHORT).show()
                }

            })
        }
        dialog?.show()
    }
}