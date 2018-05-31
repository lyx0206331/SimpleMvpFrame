package com.adrian.simplemvp

import com.adrian.simplemvp.base.BasePresenterKt

class MvpPresenterKt : BasePresenterKt<MvpViewKt>() {

    fun getData(param: String) {
        if (!isViewAttached()) {
            return
        }
        getView()?.showLoading()

        MvpModelKt.getData(param, object : MvpCallbackKt<String> {
            override fun onSuccess(data: String) {
                if (isViewAttached()) {
                    getView()?.showData(data)
                }
            }

            override fun onFailure(msg: String) {
                if (isViewAttached()) {
                    getView()?.showToast(msg)
                }
            }

            override fun onError() {
                if (isViewAttached()) {
                    getView()?.showError()
                }
            }

            override fun onComplete() {
                if (isViewAttached()) {
                    getView()?.hideLoading()
                }
            }
        })

    }
}