package com.adrian.transitionanimtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.adrian.transitionanimtest.base.BaseActivity;
import com.adrian.transitionanimtest.demo.fab.FabActivity;
import com.adrian.transitionanimtest.demo.fragment.FActivity;
import com.adrian.transitionanimtest.demo.image.ImageActivity;
import com.adrian.transitionanimtest.demo.intent.ForResultActivity;
import com.adrian.transitionanimtest.demo.intent.IntentActivity;
import com.adrian.transitionanimtest.demo.recyclerview.RvActivity;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.btn_image)
    Button btnImage;
    @Bind(R.id.btn_recycleview)
    Button btnRecycleview;
    @Bind(R.id.btn_fab)
    Button btnFab;
    @Bind(R.id.btn_fragment)
    Button btnFragment;
    @Bind(R.id.btn_intent)
    Button btnIntent;
    @Bind(R.id.btn_for_result)
    Button btnForResult;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }


    @OnClick({R.id.btn_image, R.id.btn_recycleview, R.id.btn_fab, R.id.btn_fragment, R.id.btn_intent, R.id.btn_for_result})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_image:
                gotoNextActivity(ImageActivity.class);
                break;
            case R.id.btn_recycleview:
                gotoNextActivity(RvActivity.class);
                break;
            case R.id.btn_fab:
                gotoNextActivity(FabActivity.class);
                break;
            case R.id.btn_fragment:
                gotoNextActivity(FActivity.class);
                break;
            case R.id.btn_intent:
                gotoNextActivity(IntentActivity.class);
                break;
            case R.id.btn_for_result:
                gotoNextActivity(ForResultActivity.class);
                break;
        }
    }

    private void gotoNextActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);

    }

}