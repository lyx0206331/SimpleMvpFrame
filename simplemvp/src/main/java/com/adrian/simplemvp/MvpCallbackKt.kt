package com.adrian.simplemvp

interface MvpCallbackKt<T> {

    /**
     * 数据请求成功
     */
    fun onSuccess(data: T)

    /**
     * 请求成功但返回非正常数据
     */
    fun onFailure(msg: String)

    /**
     * 请求失败。如无网络，无权限，抛异常等
     */
    fun onError()

    /**
     * 请求结束。无论成功与否，都执行此方法
     * 如隐藏加载状态UI
     */
    fun onComplete()
}