package com.adrian.simplemvp;

import com.adrian.simplemvp.base.BasePresenter;

public class MvpPresenter extends BasePresenter<MvpView> {

    public void getData(String param) {
        if (!isViewAttached()) {
            return;
        }
        getView().showLoading();

        MvpModel.getNetData(param, new MvpCallback<String>() {
            @Override
            public void onSuccess(String data) {
                if (isViewAttached()) {
                    getView().showData(data);
                }
            }

            @Override
            public void onFailure(String msg) {
                if (isViewAttached()) {
                    getView().showToast(msg);
                }
            }

            @Override
            public void onError() {
                if (isViewAttached()) {
                    getView().showErr();
                }
            }

            @Override
            public void onComplete() {
                if (isViewAttached()) {
                    getView().hideLoading();
                }
            }
        });
    }
}
