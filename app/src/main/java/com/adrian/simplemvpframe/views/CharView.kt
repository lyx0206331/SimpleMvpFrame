package com.adrian.simplemvpframe.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.adrian.simplemvpframe.R

/**
 * date:2018/6/6
 * author：RanQing
 * description：
 */
class CharView : View {

    private var leftColr: Int = Color.BLACK
    private var rightColor: Int = Color.BLACK
    private var lineColor: Int = Color.BLACK

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        val array: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CharView, defStyleAttr, 0)
    }
}