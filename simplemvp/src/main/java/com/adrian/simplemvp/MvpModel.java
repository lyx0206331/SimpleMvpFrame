package com.adrian.simplemvp;

import android.os.Handler;

import com.adrian.simplemvp.base.BaseModel;

public class MvpModel extends BaseModel<String> {

    public static void getNetData(final String param, final MvpCallback<String> callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (param) {
                    case "normal":
                        callback.onSuccess(param + ":请求参数成功");
                        break;
                    case "failure":
                        callback.onFailure(param + ":请求参数失败");
                        break;
                    case "error":
                        callback.onError();
                        break;
                }
                callback.onComplete();
            }
        }, 2000);
    }

    @Override
    public void execute(final MvpCallback<String> callback) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (mParams[0]) {
                    case "normal":
                        callback.onSuccess(mParams[0] + ":请求参数成功");
                        break;
                    case "failure":
                        callback.onFailure(mParams[0] + ":请求参数失败");
                        break;
                    case "error":
                        callback.onError();
                        break;
                }
                callback.onComplete();
            }
        }, 2000);
    }
}
