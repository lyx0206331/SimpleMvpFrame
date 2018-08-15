package com.adrian.transitionanimtest.demo.fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.adrian.transitionanimtest.R;
import com.adrian.transitionanimtest.base.BaseActivity;

import butterknife.Bind;
import immortalz.me.library.TransitionsHeleper;
import immortalz.me.library.bean.InfoBean;
import immortalz.me.library.method.ColorShowMethod;

/**
 * Created by Mr_immortalZ on 2016/10/29.
 * email : mr_immortalz@qq.com
 */

public class FDetailActivity extends BaseActivity {
    @Bind(R.id.container)
    LinearLayout container;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fragment_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, DetailFragment.newInstance())
                .commit();
        final TransitionsHeleper.TransitionBuilder builder = TransitionsHeleper.build(this);
        builder.setShowMethod(new ColorShowMethod(R.color.bg_purple, R.color.bg_teal) {
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

                    @Override
                    public void loadTargetView(InfoBean bean, View targetView) {

                    }
                })
                .setExposeColor(getResources().getColor(R.color.bg_teal))
                .show();

    }
}
