package com.adrian.simplemvp.base;


import com.adrian.simplemvp.MvpCallback;

public abstract class BaseModel<T> {

    protected String[] mParams;

    /**
     * 设置数据请求参数
     * @param args
     * @return
     */
    public BaseModel params(String... args) {
        this.mParams = args;
        return this;
    }

    public abstract void execute(MvpCallback<T> callback);

    /**
     * 执行Get网络请求
     * @param url
     * @param callback
     */
    protected void requestGetAPI(String url, MvpCallback<T> callback) {
        //具体网络请求实现
    }

    /**
     * 执行Post网络请求
     * @param url
     * @param callback
     */
    protected void requestPostAPI(String url, MvpCallback<T> callback) {
        //具体网络请求实现
    }
}
