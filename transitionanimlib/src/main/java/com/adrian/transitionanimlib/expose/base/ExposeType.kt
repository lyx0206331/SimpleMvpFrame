package com.adrian.transitionanimlib.expose.base

/**
 * date:2018/8/9 11:45
 * author：RanQing
 * description：
 */
enum class ExposeType {
    DEFAULT(0),
    INFLATE(1), ;

    private val value: Int

    constructor(value: Int) {
        this.value = value
    }

    internal fun toInt(): Int {
        return value
    }
}