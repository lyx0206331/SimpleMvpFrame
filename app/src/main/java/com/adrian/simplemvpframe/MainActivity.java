package com.adrian.simplemvpframe;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.adrian.circleprogressbarlib.CircleProgressBar;
import com.adrian.simplemvp.MvpPresenter;
import com.adrian.simplemvp.MvpView;
import com.adrian.simplemvp.base.BaseActivity;
import com.adrian.simplemvpframe.utils.SimpleOpUtil;
import com.adrian.simplemvpframe.views.chart_view.SuperCircleView;

import java.math.BigDecimal;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends BaseActivity implements MvpView {

    SuperCircleView mSuperCircleView;
    TextView textView;
    CircleProgressBar mCircleProgressbar;

    TextView text;
    ImageView ivShapeTest;
    MvpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivShapeTest = findViewById(R.id.ivShapeTest);
//        GradientDrawable drawable = new GradientDrawable();
//        drawable.setSize(30, 30);
//        drawable.setCornerRadius(15);
//        drawable.setColor(Color.BLUE);
//        drawable.setShape(GradientDrawable.OVAL);
//        drawable.setBounds(0, 0, 30, 30);
//        ivShapeTest.setImageDrawable(drawable);
//        GradientDrawable background = new GradientDrawable();
//        background.setSize(50, 50);
//        background.setStroke(2, Color.GREEN);
//        background.setCornerRadius(25f);
//        background.setShape(GradientDrawable.RING);
//        ivShapeTest.setBackground(background);

        initSuperCircle();

//        Log.e("REALROUND", "parseDouble 0.125: " + realRounding(0.125, 2));
//        Log.e("REALROUND", "parseDouble 5.749: " + realRounding(5.749, 1));

        ArrayList<Integer> primes = getPrimeNumbers(2, 100);

        for (int prime :
                primes) {
            Log.e("PRIME", "获取2 - 100之间的质数:" + prime);
        }

        initCircleProgressbar();

        text = (TextView) findViewById(R.id.text);

        //初始化Presenter
        presenter = new MvpPresenter();
        presenter.attachView(this);

        SimpleOpUtil.INSTANCE.test();

        formatContent("<@>82_zhao3哎呦不错</@>sd枯<@>83_zhao3哎呦不错</@>asdfgasd<@>84_zhao3哎呦不错</@>花飘万家雪");
        parseEventId("http://event.2bulu.com/d-5d282827688cd06178c15ab6.htm?staticSource=%7B%22userInfo%22%3A%7B%22sdkLevel%22%3A%2212.3.1%22%2C%22cityName%22%3A%22%E8%A5%BF%E5%AE%89%22%2C%22p_terminalType%22%3A%223%22%2C%22netType%22%3A%224%22%2C%22channel%22%3A%22appStore%22%2C%22imei%22%3A%22%22%2C%22p_appVersion%22%3A%226.4.2.6%22%2C%22imsi%22%3A%22%22%2C%22deviceName%22%3A%22iPhone+7+Plus%22%2C%22userId%22%3A%221974388%22%2C%22mac%22%3A%22%22%2C%22openUDID%22%3A%221FDD0C9B-61FB-48EE-A4B2-CDBE2C5107D3%22%7D%2C%22record%22%3A%22%3CPage%3ANullPage%3E%3CSection%3AAdvertise1%3E%22%7D");
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

    private String formatContent(String content) {
        String reg = "<@>\\d+_";

        String result = content.replaceAll(reg, "@").replaceAll("</@>", " ");

        Log.e("REG", "result: " + result);

        return result;
    }

    private boolean parseEventId(String eventUrl) {
        String regex = "(http://event.2bulu.com/d-)([0-9a-z]+)(\\.htm.*)";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(eventUrl);

        if (m.matches()) {
            Log.e("REG", "当前是赛事链接");
            int groupCount = m.groupCount();
            Log.e("REG", "groupCount:" + groupCount);
            for (int i = 0; i < groupCount; i++) {
                Log.e("REG", m.group(i));
            }
            return true;
        } else {
            Log.e("REG", "当前非赛事链接");
            return false;
        }
    }

    /**
     * 获取一定范围内的质数
     *
     * @param start
     * @param end
     * @return
     */
    private ArrayList getPrimeNumbers(int start, int end) {
        if (start < 2 || start >= end) {
            throw new InvalidParameterException("参数不合法，请传入不小于2的整数");
        }
        ArrayList<Integer> primeNums = new ArrayList<>();
        if (start == 2) {
            primeNums.add(2);
        }
        //偶数不用计算
        for (int i = start % 2 == 0 ? start + 1 : start; i < end; i += 2) {
            int centerValue = (i + 2) / 2;
            boolean isPrime = true;
            for (int j = 2; j <= centerValue; j++) {
                if (i % j == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                primeNums.add(i);
            }
        }
        return primeNums;
    }

    /**
     * 四舍五入
     *
     * @param value  原始值
     * @param retain 保留位数
     * @return
     */
    private double realRounding(double value, int retain) {
        return new BigDecimal(value).setScale(retain, BigDecimal.ROUND_HALF_UP).doubleValue();
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
                startActivity(new Intent(MainActivity.this, BitmapActivity.class));
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
