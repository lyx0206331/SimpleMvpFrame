package com.adrian.simplemvp

import android.os.Handler
import com.adrian.simplemvp.base.BaseModelKt

class MvpModelKt(val mParam: String) : BaseModelKt<String>(mParam) {

    companion object {
        fun getData(param: String, callback: MvpCallbackKt<String>) {
            Handler().postDelayed({
                when (param) {
                    "normal" -> callback.onSuccess("$param:请求参数成功")
                    "failure" -> callback.onFailure("$param:请求参数失败")
                    "error" -> callback.onError()
                }
                callback.onComplete()
            }, 2000)
        }
    }

    override fun execute(callback: MvpCallback<String>) {
        Handler().postDelayed({
            when (mParam) {
                "normal" -> callback.onSuccess("$mParam:请求参数成功")
                "failure" -> callback.onFailure("$mParam:请求参数失败")
                "error" -> callback.onError()
            }
            callback.onComplete()
        }, 2000)
    }
}