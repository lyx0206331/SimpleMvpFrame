package com.adrian.simplemvpframe;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adrian.simplemvp.MvpPresenter;
import com.adrian.simplemvp.MvpView;
import com.adrian.simplemvp.base.BaseActivity;
import com.adrian.simplemvpframe.views.DHealthyProgressView;
import com.adrian.simplemvpframe.views.StepCounterProgressKt;
import com.adrian.simplemvpframe.views.SuperCircleView;

public class MainActivity extends BaseActivity implements MvpView {

    SuperCircleView mSuperCircleView;
    TextView textView;

    private StepCounterProgressKt healthyProgressView;
    private EditText et_input;
    private Button bt_show;
    private Button bt_reset;
    private TextView tv_value;

    TextView text;
    MvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSuperCircle();

        initStepCounter();

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
        et_input = (EditText) findViewById(R.id.et_input);
        bt_show = (Button) findViewById(R.id.bt_show);
        tv_value = (TextView) findViewById(R.id.tv_value);
        bt_reset = findViewById(R.id.btn_reset);

        healthyProgressView = (StepCounterProgressKt) findViewById(R.id.simple);

        healthyProgressView.beginContinue(true);

        healthyProgressView.setInterpolator(new AccelerateInterpolator());

        healthyProgressView.setValue(17);

        bt_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    float value = Float.valueOf(et_input.getText().toString().trim());
                    if (value > 100) {
                        throw new NumberFormatException();
                    }
                    healthyProgressView.setValue(value);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "请输入有效进度", Toast.LENGTH_SHORT).show();
                }


            }
        });

        bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                healthyProgressView.reset();
            }
        });

        healthyProgressView.setOnValueChangeListener(new StepCounterProgressKt.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
                tv_value.setText((int) value + "");
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
