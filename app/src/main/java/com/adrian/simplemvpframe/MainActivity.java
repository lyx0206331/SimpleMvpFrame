package com.adrian.simplemvpframe;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adrian.simplemvp.MvpPresenter;
import com.adrian.simplemvp.MvpView;
import com.adrian.simplemvp.base.BaseActivity;
import com.adrian.simplemvpframe.views.ChartView;
import com.adrian.simplemvpframe.views.CustomChartView;
import com.adrian.simplemvpframe.views.DHealthyProgressView;
import com.adrian.simplemvpframe.views.StepCounterProgressKt;
import com.adrian.simplemvpframe.views.SuperCircleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends BaseActivity implements MvpView {

    SuperCircleView mSuperCircleView;
    TextView textView;

    private StepCounterProgressKt healthyProgressView;
    private EditText et_input;
    private Button bt_show;
    private Button bt_reset;
    private TextView tv_value;

    private ChartView myChartView;
    private LinearLayout relativeLayout;
    private LinearLayout llChart;
    private List<Float> chartList;

    TextView text;
    MvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initSuperCircle();

        initStepCounter();

        initChartView();

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

    private void initChartView() {
        myChartView = findViewById(R.id.chart_view1);

        myChartView.setLeftColorBottom(getResources().getColor(R.color.leftColorBottom));
        myChartView.setLeftColor(getResources().getColor(R.color.leftColor));
        myChartView.setRightColor(getResources().getColor(R.color.rightColor));
        myChartView.setRightColorBottom(getResources().getColor(R.color.rightBottomColor));
        myChartView.setSelectLeftColor(getResources().getColor(R.color.selectLeftColor));
        myChartView.setSelectRightColor(getResources().getColor(R.color.selectRightColor));
        myChartView.setLineColor(getResources().getColor(R.color.xyColor));
        chartList = new ArrayList<>();

        relativeLayout = (LinearLayout) findViewById(R.id.linearLayout);
        relativeLayout.removeView(llChart);
        Random random = new Random();
        while (chartList.size() < 24) {
            int randomInt = random.nextInt(100);
            chartList.add((float) randomInt);
        }
        myChartView.setList(chartList);
        myChartView.setListener(new ChartView.OnClickNumberListener() {
            @Override
            public void clickNumber(int number, int x, int y) {
                relativeLayout.removeView(llChart);
                //反射加载点击柱状图弹出布局
                llChart = (LinearLayout) LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_shouru_zhichu, null);
                TextView tvZhichu = (TextView) llChart.findViewById(R.id.tv_zhichu);
                TextView tvShouru = (TextView) llChart.findViewById(R.id.tv_shouru);
                tvZhichu.setText((number + 1) + "月支出" + " " + chartList.get(number * 2));
                tvShouru.setText("收入: " + chartList.get(number * 2 + 1));
                llChart.measure(0, 0);//调用该方法后才能获取到布局的宽度
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.leftMargin = x - 100;
                if (x - 100 < 0) {
                    params.leftMargin = 0;
                } else if (x - 100 > relativeLayout.getWidth() - llChart.getMeasuredWidth()) {
                    //设置布局距左侧屏幕宽度减去布局宽度
                    params.leftMargin = relativeLayout.getWidth() - llChart.getMeasuredWidth();
                }
                llChart.setLayoutParams(params);
                relativeLayout.addView(llChart);
            }
        });

        String[] xLabel = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
                "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27",
                "28", "29", "30", "31"};
        String[] yLabel = {"0", "100", "200", "300", "400", "500", "600", "700", "800", "900"};
        int[] data1 = {300, 500, 550, 500, 300, 700, 800, 750, 550, 600, 400, 300, 400, 600, 500,
                700, 300, 500, 550, 500, 300, 700, 800, 750, 550, 600, 400, 300, 400, 600, 500};
        List<int[]> data = new ArrayList<>();
        data.add(data1);
        List<Integer> color = new ArrayList<>();
        color.add(R.color.leftColor);
        color.add(R.color.rightColor);
        color.add(R.color.colorPrimary);
        relativeLayout.addView(new CustomChartView(this, xLabel, yLabel, data, color));
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
