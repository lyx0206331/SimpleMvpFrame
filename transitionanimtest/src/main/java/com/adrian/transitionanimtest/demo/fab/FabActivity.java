package com.adrian.transitionanimtest.demo.fab;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Button;

import com.adrian.transitionanimtest.R;
import com.adrian.transitionanimtest.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;
import immortalz.me.library.TransitionsHeleper;

/**
 * Created by Mr_immortalZ on 2016/10/29.
 * email : mr_immortalz@qq.com
 */

public class FabActivity extends BaseActivity {
    @Bind(R.id.btn_circle)
    FloatingActionButton btnCircle;
    @Bind(R.id.btn_no)
    FloatingActionButton btnNo;
    @Bind(R.id.btn)
    Button btn;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fab;
    }


    @OnClick({R.id.btn_no, R.id.btn_circle, R.id.btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_no:
                TransitionsHeleper.startActivity(FabActivity.this, FabNoCircleActivity.class, btnNo);
                break;
            case R.id.btn_circle:
                TransitionsHeleper.startActivity(FabActivity.this, FabCircleActivity.class, btnCircle);
                break;
            case R.id.btn:
                TransitionsHeleper.startActivity(FabActivity.this, ButtonActivity.class, btn);
                break;
        }
    }

}
