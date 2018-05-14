package com.adrian.simplemvp.base;

public class BasePresenter<V extends IBaseView> {

    private V mvpView;

    /**
     * 绑定View,初始化时调用
     * @param mvpView
     */
    public void attachView(V mvpView) {
        this.mvpView = mvpView;
    }

    /**
     * 解绑View，一般在onDestroy中调用
     */
    public void detachView() {
        this.mvpView = null;
    }

    /**
     * 是否绑定View
     * @return
     */
    public boolean isViewAttached() {
        return mvpView != null;
    }

    public V getView() {
        return mvpView;
    }
}
