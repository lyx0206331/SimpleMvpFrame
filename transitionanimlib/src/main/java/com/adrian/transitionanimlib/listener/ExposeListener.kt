package com.adrian.transitionanimlib.listener

/**
 * date:2018/8/9 12:25
 * author：RanQing
 * description：
 */
interface ExposeListener {

    fun onExposeStart()

    fun onExposeProgrees(progress: Float)

    fun onExposeEnd()
}