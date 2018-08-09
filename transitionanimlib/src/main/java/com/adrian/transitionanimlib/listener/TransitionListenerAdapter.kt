package com.adrian.transitionanimlib.listener

/**
 * date:2018/8/9 16:04
 * author：RanQing
 * description：
 */
abstract class TransitionListenerAdapter : TransitionListener {
    override fun onExposeStart() {}

    override fun onExposeProgress(progress: Float) {}

    override fun onExposeEnd() {}
}