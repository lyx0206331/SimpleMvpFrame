package com.adrian.transitionanimlib.method

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import com.adrian.transitionanimlib.bean.InfoBean
import com.adrian.transitionanimlib.expose.base.ExposeView

/**
 * date:2018/8/9 16:26
 * author：RanQing
 * description：
 */
class NoneShowMethod() : ShowMethod() {
    override fun translate(bean: InfoBean<Any>, parent: ExposeView, child: View) {
        set.playTogether(ObjectAnimator.ofFloat(child, "scaleY", 1f))
        set.interpolator = AccelerateInterpolator()
        set.setDuration(showDuration).start()
    }

    override fun loadPlaceholder(bean: InfoBean<Any>, placeholder: ImageView) {
        val set = AnimatorSet()
        set.playTogether(
                ObjectAnimator.ofFloat(placeholder, "rotation", 0f, 180f),
                ObjectAnimator.ofFloat(placeholder, "scaleX", 1f, 0f),
                ObjectAnimator.ofFloat(placeholder, "scaleY", 1f, 0f)
        )
        set.interpolator = AccelerateInterpolator()
        set.setDuration(showDuration / 4 * 5).start()
    }

    override fun loadTargetView(bean: InfoBean<Any>, targetView: View) {}
}