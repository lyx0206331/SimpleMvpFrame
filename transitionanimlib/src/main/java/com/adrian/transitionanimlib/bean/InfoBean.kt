package com.adrian.transitionanimlib.bean

import android.graphics.Bitmap
import android.graphics.Rect

/**
 * date:2018/8/9 11:30
 * author：RanQing
 * description：
 */
class InfoBean<ModelType> {
    var statusBarHeight: Int = 0
    var titleHeight: Int = 0
    //image's url or resource id
    var bitmap: Bitmap? = null
    private var load: ModelType? = null

    var translationY: Int = 0
    var translationX: Int = 0
    //origin view
    var originRect = Rect()
    var originWidth: Int = 0
    var originHeight: Int = 0
    //target view
    var targetRect = Rect()
    var targetWidth: Int = 0
    var targetHeight: Int = 0
    //Content Window's size
    var windowWidth: Int = 0
    var windowHeight: Int = 0

    var scale: Float = 0.toFloat()

    fun getLoad(): ModelType? {
        return load
    }

    fun setLoad(load: ModelType?) {
        this.load = load
    }
}