package com.adrian.transitionanimlib.method

import android.animation.AnimatorSet
import android.view.View
import android.widget.ImageView
import com.adrian.transitionanimlib.bean.InfoBean
import com.adrian.transitionanimlib.expose.base.ExposeView

/**
 * date:2018/8/9 16:19
 * author：RanQing
 * description：
 */
abstract class ShowMethod {
    companion object {
        const val DEFAULT_DURATION = 240L
    }

    var set: AnimatorSet = AnimatorSet()
    var showDuration = DEFAULT_DURATION
    abstract fun translate(bean: InfoBean<Any>, parent: ExposeView?, child: View?)
    fun reviseInfo(info: InfoBean<Any>) {}

    /**
     * load placeholder which just a temp view.
     * the placeholder is show when it's translating.
     */
    abstract fun loadPlaceholder(bean: InfoBean<Any>, placeholder: ImageView)

    /**
     * load targetView
     */
    abstract fun loadTargetView(bean: InfoBean<Any>, targetView: View?)

}