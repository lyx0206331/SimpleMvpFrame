package com.adrian.transitionanimlib.method

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.adrian.transitionanimlib.R
import com.adrian.transitionanimlib.bean.InfoBean
import com.adrian.transitionanimlib.expose.base.ExposeView

/**
 * date:2018/8/9 16:35
 * author：RanQing
 * description：
 */
abstract class ColorShowMethod(var startColor: Int, var endColor: Int) : ShowMethod() {

    override fun translate(bean: InfoBean<Any>, parent: ExposeView, child: View) {
        startColor = if (startColor != 0) {
            parent.resources.getColor(startColor)
        } else {
            parent.resources.getColor(R.color.transitionhelper_showmethod_start_color)
        }

        endColor = if (endColor != 0) {
            parent.resources.getColor(endColor)
        } else {
            parent.resources.getColor(R.color.transitionhelper_showmethod_end_color)
        }
        val colorAnimator = ObjectAnimator.ofInt(parent, "backgroundColor", startColor, endColor)
        colorAnimator.setEvaluator(ArgbEvaluator())
        set.playTogether(
                ObjectAnimator.ofFloat(child, "translationX", 0f, -bean.translationX.toFloat()),
                ObjectAnimator.ofFloat(child, "translationY", 0f, -bean.translationY.toFloat()),
                ObjectAnimator.ofFloat(child, "scaleX", 1f / bean.scale),
                ObjectAnimator.ofFloat(child, "scaleY", 1f / bean.scale),
                colorAnimator
        )
        set.interpolator = AccelerateInterpolator()
        set.setDuration(showDuration).start()
    }
}