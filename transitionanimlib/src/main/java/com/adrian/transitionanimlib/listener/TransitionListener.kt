package com.adrian.transitionanimlib.listener

/**
 * date:2018/8/9 16:02
 * author：RanQing
 * description：
 */
interface TransitionListener {

    fun onExposeStart()
    fun onExposeProgress(progress: Float)
    fun onExposeEnd()
}