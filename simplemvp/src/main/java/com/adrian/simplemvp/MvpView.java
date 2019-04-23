package com.adrian.simplemvp;

import com.adrian.simplemvp.base.IBaseView;

/**
 * @author Administrator
 */
public interface MvpView extends IBaseView {

    /**
     * @param data
     */
    void showData(String data);

}
