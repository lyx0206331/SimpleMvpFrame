package com.adrian.simplemvpframe

import android.app.Application

/**
 * date:2018/6/23
 * author：RanQing
 * description：
 */
class MyApplication : Application() {

    companion object {
        @JvmField
        var instance: MyApplication? = null
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
    }
}