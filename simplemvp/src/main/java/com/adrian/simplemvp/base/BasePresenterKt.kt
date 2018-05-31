package com.adrian.simplemvp.base

open class BasePresenterKt<V : IBaseViewKt> {

    private var mvpView: V? = null

    /**
     * 绑定View,初始化时调用
     * @param mvpView
     */
    fun attachView(mvpView: V) {
        this.mvpView = mvpView
    }

    /**
     * 解绑View，一般在onDestroy中调用
     */
    fun detachView() {
        this.mvpView = null
    }

    /**
     * 是否绑定View
     * @return
     */
    fun isViewAttached(): Boolean {
        return mvpView != null
    }

    fun getView(): V? {
        return mvpView
    }
}