package com.adrian.simplemvp.base

import android.content.Context

interface IBaseViewKt {
    /**
     * 显示加载状态UI
     */
    fun showLoading()

    /**
     * 隐藏加载状态UI
     */
    fun hideLoading()

    /**
     * 显示Toast
     */
    fun showToast(msg: String)

    /**
     * 显示错误信息
     */
    fun showError()

    /**
     * 获取上下文
     */
    fun getContext(): Context
}