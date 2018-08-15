package com.adrian.transitionanimtest.demo.fragment;

import android.support.design.widget.FloatingActionButton;

import com.adrian.transitionanimtest.R;
import com.adrian.transitionanimtest.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;
import immortalz.me.library.TransitionsHeleper;

/**
 * Created by Mr_immortalZ on 2016/10/29.
 * email : mr_immortalz@qq.com
 */

public class FActivity extends BaseActivity {
    @Bind(R.id.btn_circle)
    FloatingActionButton btnCommit;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fragment;
    }

    @OnClick(R.id.btn_circle)
    public void onClick() {
        TransitionsHeleper.startActivity(this, FDetailActivity.class, btnCommit);
    }

}
