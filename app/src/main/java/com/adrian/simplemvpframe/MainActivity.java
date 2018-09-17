package com.adrian.simplemvpframe;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.adrian.circleprogressbarlib.CircleProgressBar;
import com.adrian.simplemvp.MvpPresenter;
import com.adrian.simplemvp.MvpView;
import com.adrian.simplemvp.base.BaseActivity;
import com.adrian.simplemvpframe.views.chart_view.SuperCircleView;

public class MainActivity extends BaseActivity implements MvpView {

    SuperCircleView mSuperCircleView;
    TextView textView;
    CircleProgressBar mCircleProgressbar;

    TextView text;
    MvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSuperCircle();

        initStepCounter();

        initCircleProgressbar();

        text = (TextView) findViewById(R.id.text);

        //初始化Presenter
        presenter = new MvpPresenter();
        presenter.attachView(this);
    }

    private void initSuperCircle() {
        textView = (TextView) findViewById(R.id.tv);
        mSuperCircleView = (SuperCircleView) findViewById(R.id.superview);
        mSuperCircleView.setShowSelect(false);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 100);
        valueAnimator.setTarget(textView);
        valueAnimator.setDuration(2000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int i = Integer.valueOf(String.valueOf(animation.getAnimatedValue()));
                textView.setText(i + "");
                mSuperCircleView.setSelect((int) (360 * (i / 100f)));
            }
        });
        valueAnimator.start();
    }

    private void initStepCounter() {
    }

    private void initCircleProgressbar() {
        mCircleProgressbar = findViewById(R.id.circle_progressbar);
//        mCircleProgressbar.startAnimator(1000, 0, 100, 0);
        mCircleProgressbar.setMOnPressedListener(new CircleProgressBar.OnPressedListener() {
            @Override
            public void onPressStart() {

            }

            @Override
            public void onPressProcess(int i) {

            }

            @Override
            public void onPressInterrupt(int i) {
                startActivity(new Intent(MainActivity.this, TestSnackbarUtilsActivity.class));
            }

            @Override
            public void onPressEnd() {
                startActivity(new Intent(MainActivity.this, TestNestedScrollViewActivity.class));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    // button 点击事件调用方法
    public void getData(View view){
        presenter.getData("normal");
    }
    // button 点击事件调用方法
    public void getDataForFailure(View view){
        presenter.getData("failure");
    }
    // button 点击事件调用方法
    public void getDataForError(View view){
        presenter.getData("error");
    }

    @Override
    public void showData(String data) {
        text.setText(data);
    }
}
