package com.adrian.transitionanimlib.method

import android.animation.ObjectAnimator
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.adrian.transitionanimlib.bean.InfoBean
import com.adrian.transitionanimlib.expose.base.ExposeView

/**
 * date:2018/8/9 16:31
 * author：RanQing
 * description：
 */
abstract class InflateShowMethod(activity: Activity, layoutId: Int) : ShowMethod() {

    val inflateView: View = LayoutInflater.from(activity).inflate(layoutId, null)

    override fun translate(bean: InfoBean<Any>, parent: ExposeView?, child: View?) {
        set.playTogether(
                ObjectAnimator.ofFloat(child, "translationX", 0f, -bean.translationX.toFloat()),
                ObjectAnimator.ofFloat(child, "translationY", 0f, -bean.translationY.toFloat()),
                ObjectAnimator.ofFloat(child, "scaleX", 1f, 1f / bean.scale),
                ObjectAnimator.ofFloat(child, "scaleY", 1f, 1f / bean.scale)
        )
        set.interpolator = AccelerateInterpolator()
        set.setDuration(showDuration).start()
    }
}