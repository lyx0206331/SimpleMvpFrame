package com.adrian.simplemvpframe

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.adrian.simplemvp.MvpPresenterKt
import com.adrian.simplemvp.MvpViewKt
import com.adrian.simplemvp.base.BaseActivityKt
import com.adrian.simplemvpframe.views.MyDialog

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
            dialog = MyDialog(this)
        }
        dialog?.show()
    }
}