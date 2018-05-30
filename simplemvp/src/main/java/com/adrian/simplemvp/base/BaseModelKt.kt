package com.adrian.simplemvp.base

import com.adrian.simplemvp.MvpCallback

abstract class BaseModelKt<T>(vararg mParams: String) {


    abstract fun execute(callback: MvpCallback<T>)

    /**
     * 执行Get网络请求
     * @param url
     * @param callback
     */
    protected fun requestGetAPI(url: String, callback: MvpCallback<T>) {
        //具体网络请求实现
    }

    /**
     * 执行Post网络请求
     * @param url
     * @param callback
     */
    protected fun requestPostAPI(url: String, callback: MvpCallback<T>) {
        //具体网络请求实现
    }
}