package com.adrian.simplemvp;

public interface MvpCallback<T> {

    /**
     * 数据请求成功
     * @param data
     */
    void onSuccess(T data);

    /**
     * 请求成功但返回非正常数据
     * @param msg
     */
    void onFailure(String msg);

    /**
     * 请求失败。如无网络，无权限，抛异常等
     */
    void onError();

    /**
     * 请求结束。无论成功与否，都执行此方法
     * 如隐藏加载状态UI
     */
    void onComplete();
}
