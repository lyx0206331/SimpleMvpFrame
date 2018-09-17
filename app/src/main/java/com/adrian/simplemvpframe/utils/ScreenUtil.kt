package com.adrian.simplemvpframe.utils

import android.R.attr.data
import android.app.Activity
import android.app.ActivityGroup
import android.content.Context
import android.util.TypedValue.complexToDimensionPixelSize
import android.os.Build.VERSION_CODES.HONEYCOMB
import android.os.Build.VERSION.SDK_INT
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.content.Context.WINDOW_SERVICE
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


/**
 * date:2018/9/17 17:15
 * author：RanQing
 * description：
 */
object ScreenUtil {
    private var mStatusHeight = -1

    fun dp2px(ctx: Context, dpValue: Float): Int {
        val density = ctx.resources.displayMetrics.density
        return (dpValue * density + 0.5f).toInt()
    }

    fun sp2px(ctx: Context, spValue: Float): Int {
        val scaledDensity = ctx.resources.displayMetrics.scaledDensity
        return (spValue * scaledDensity + 0.5f).toInt()
    }

    /**
     * 获取屏幕的宽度
     * @param context
     * @return
     */
    fun getScreenWidth(context: Context): Int {
        val manager = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        return display.getWidth()
    }

    /**
     * 获取屏幕的高度
     * @param context
     * @return
     */
    fun getScreenHeight(context: Context): Int {
        val manager = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        return display.height
    }

    /**
     * 获得状态栏的高度
     * @param context
     * @return mStatusHeight
     */
    fun getStatusHeight(context: Context): Int {
        if (mStatusHeight != -1) {
            return mStatusHeight
        }
        try {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                mStatusHeight = context.resources.getDimensionPixelSize(resourceId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mStatusHeight
    }


    /**
     * 获取当前屏幕截图，不包含状态栏
     * @param activity
     * @return bp
     */
    fun snapShotWithoutStatusBar(activity: Activity): Bitmap? {
        val view = activity.getWindow().getDecorView()
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bmp = view.drawingCache ?: return null
        val frame = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(frame)
        val statusBarHeight = frame.top
        val bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, bmp.width, bmp.height - statusBarHeight)
        view.destroyDrawingCache()
        view.isDrawingCacheEnabled = false

        return bp
    }

    /**
     * 获取actionbar的像素高度，默认使用android官方兼容包做actionbar兼容
     *
     * @return
     */
    fun getActionBarHeight(context: Context): Int {
        var actionBarHeight = 0
        if (context is AppCompatActivity && (context as AppCompatActivity).getSupportActionBar() != null) {
            Log.d("isAppCompatActivity", "==AppCompatActivity")
            actionBarHeight = (context as AppCompatActivity).getSupportActionBar()!!.getHeight()
        } else if (context is Activity && (context as Activity).getActionBar() != null) {
            Log.d("isActivity", "==Activity")
            actionBarHeight = (context as Activity).getActionBar().getHeight()
        } else if (context is ActivityGroup) {
            Log.d("ActivityGroup", "==ActivityGroup")
            if ((context as ActivityGroup).getCurrentActivity() is AppCompatActivity && ((context as ActivityGroup).getCurrentActivity() as AppCompatActivity).getSupportActionBar() != null) {
                actionBarHeight = ((context as ActivityGroup).getCurrentActivity() as AppCompatActivity).getSupportActionBar()!!.getHeight()
            } else if ((context as ActivityGroup).getCurrentActivity() is Activity && ((context as ActivityGroup).getCurrentActivity() as Activity).getActionBar() != null) {
                actionBarHeight = ((context as ActivityGroup).getCurrentActivity() as Activity).getActionBar().getHeight()
            }
        }
        if (actionBarHeight != 0)
            return actionBarHeight
        val tv = TypedValue()
        if (context.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, tv, true)) {
            if (context.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics())
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics())
        } else {
            if (context.getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics())
        }
        Log.d("actionBarHeight", "====$actionBarHeight")
        return actionBarHeight
    }


    /**
     * 设置view margin
     * @param v
     * @param l
     * @param t
     * @param r
     * @param b
     */
    fun setMargins(v: View, l: Int, t: Int, r: Int, b: Int) {
        if (v.getLayoutParams() is ViewGroup.MarginLayoutParams) {
            val p = v.getLayoutParams() as ViewGroup.MarginLayoutParams
            p.setMargins(l, t, r, b)
            v.requestLayout()
        }
    }
}