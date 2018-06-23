package com.adrian.simplemvpframe.utils;

import android.content.Context;

import com.adrian.simplemvpframe.MyApplication;

/**
 * date:2018/6/23
 * author：RanQing
 * description：
 */
public class PxUtil {

    public static float dip2px(Context context, float dipValue) {
        final float scale =
                context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    public static float dip2px(float dipValue) {
        return dip2px(MyApplication.instance, dipValue);
    }

    public static int dip2pxInt(float dipValue) {
        return (int) dip2px(dipValue);
    }

    public static float px2dip(Context context, float pxValue) {
        final float scale =
                context.getResources().getDisplayMetrics().density;
        return pxValue / scale + 0.5f;
    }

    public static float px2dip(float pxValue) {
        return px2dip(MyApplication.instance, pxValue);
    }

    public static float getDensity() {
        return MyApplication.instance.getResources().getDisplayMetrics().density;
    }
}
