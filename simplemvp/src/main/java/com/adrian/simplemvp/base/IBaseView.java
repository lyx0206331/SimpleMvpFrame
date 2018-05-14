package com.adrian.simplemvp.base;

import android.content.Context;

public interface IBaseView {

    /**
     * 显示加载状态UI
     */
    void showLoading();

    /**
     * 隐藏加载状态UI
     */
    void hideLoading();

    /**
     * 显示Toast
     * @param msg
     */
    void showToast(String msg);

    /**
     * 显示错误信息
     */
    void showErr();

    /**
     * 获取上下文
     * @return
     */
    Context getContext();
}
