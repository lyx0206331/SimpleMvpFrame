package com.adrian.transitionanimtest.demo.intent;

import android.content.Intent;
import android.widget.Button;

import com.adrian.transitionanimtest.R;
import com.adrian.transitionanimtest.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;
import immortalz.me.library.TransitionsHeleper;

/**
 * Created by Mr_immortalZ on 2016/11/1.
 * email : mr_immortalz@qq.com
 */

public class IntentActivity extends BaseActivity {
    @Bind(R.id.btn)
    Button btn;

    @Override
    public int getLayoutId() {
        return R.layout.activity_intent;
    }


    @OnClick(R.id.btn)
    public void onClick() {
        Intent intent = new Intent(this, IntentDetailActivity.class);
        intent.putExtra(IntentDetailActivity.TRANSITION_DATA, "This is immortalZ");
        TransitionsHeleper.startActivity(this, intent, btn);
    }
}
