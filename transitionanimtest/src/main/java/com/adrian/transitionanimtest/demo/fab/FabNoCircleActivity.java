package com.adrian.transitionanimtest.demo.fab;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adrian.transitionanimtest.R;
import com.adrian.transitionanimtest.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;
import immortalz.me.library.TransitionsHeleper;
import immortalz.me.library.bean.InfoBean;
import immortalz.me.library.method.NoneShowMethod;

/**
 * Created by Mr_immortalZ on 2016/10/29.
 * email : mr_immortalz@qq.com
 */

public class FabNoCircleActivity extends BaseActivity {

    @Bind(R.id.tv)
    TextView tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TransitionsHeleper.TransitionBuilder builder = TransitionsHeleper.build(this);
        builder.setShowMethod(new NoneShowMethod() {
                    @Override
                    public void loadPlaceholder(InfoBean bean, ImageView placeholder) {
                        AnimatorSet set = new AnimatorSet();
                        set.playTogether(
                                ObjectAnimator.ofFloat(placeholder, "rotation", 0, 180),
                                ObjectAnimator.ofFloat(placeholder, "scaleX", 1, 0),
                                ObjectAnimator.ofFloat(placeholder, "scaleY", 1, 0)
                        );
                        set.setInterpolator(new AccelerateInterpolator());
                        set.setDuration(showDuration / 4 * 5).start();
                    }
                })
                .show();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_fab_detail;
    }

    @OnClick(R.id.tv)
    public void onClick() {
        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
    }
}