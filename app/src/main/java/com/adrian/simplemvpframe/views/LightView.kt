package com.adrian.simplemvpframe.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.graphics.drawable.Drawable
import android.os.Build
import android.annotation.TargetApi
import android.annotation.SuppressLint
import android.support.v4.view.ViewCompat.setAlpha
import com.adrian.simplemvpframe.views.LightView
import android.content.res.TypedArray
import android.graphics.*
import com.adrian.simplemvpframe.R


/**
 * author:RanQing
 * date:2018/7/7 0007
 * description:
 **/
class LightView : View {
    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private var mPaint: Paint? = null
    private var mSrcRect: Rect? = null
    private var mDestRect: Rect? = null
    private var bmp: Bitmap? = null
    private var shadowBitmap: Bitmap? = null
    private var w: Int = 0
    private var h: Int = 0
    private fun init(context: Context?, attrs: AttributeSet?) {
        // 关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        mPaint = Paint()
        val a = context?.theme?.obtainStyledAttributes(attrs, R.styleable.LightView, 0, 0)
        val n = a!!.indexCount
        var src_resource: Drawable?
        for (i in 0 until n) {
            val attr = a.getIndex(i)
            when (attr) {
                R.styleable.LightView_light_color -> mPaint!!.setColor(a.getColor(attr, Color.WHITE))
                R.styleable.LightView_light_radius -> {
                    mPaint!!.maskFilter = BlurMaskFilter(a.getFloat(attr, 5f), BlurMaskFilter.Blur.OUTER)
                    mPaint!!.alpha = 180   // 可根据实际情况调整
                }
                R.styleable.LightView_light_src -> {
                    src_resource = a.getDrawable(attr)
                    bmp = drawableToBitmap(src_resource)
                }
            }
        }

    }


    @SuppressLint("DrawAllocation")
    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        w = this.measuredWidth
        h = this.measuredHeight
        // 获取位图的Alpha通道图
        shadowBitmap = Bitmap.createScaledBitmap(bmp!!.extractAlpha(), w, h, true)
        bmp = Bitmap.createScaledBitmap(bmp!!, w, h, true)
        mSrcRect = Rect(0, 0, shadowBitmap!!.width, shadowBitmap!!.height)
        mDestRect = Rect(0, 0, shadowBitmap!!.width, shadowBitmap!!.height)
        canvas.drawBitmap(shadowBitmap, mSrcRect, mDestRect, mPaint)
        canvas.drawBitmap(bmp, mSrcRect, mSrcRect, null)
    }

    fun drawableToBitmap(drawable: Drawable?): Bitmap {
        val bitmap = Bitmap.createBitmap(
                drawable!!.intrinsicWidth,
                drawable.intrinsicHeight,
                if (drawable.opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }

    /**
     * 重写onMeasure，解决在wrap_content下与match_parent效果一样的问题
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val h = w
        val w = h
        val widthSpecMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = View.MeasureSpec.getMode(heightMeasureSpec)
        if (widthSpecMode == View.MeasureSpec.AT_MOST && heightSpecMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(w, h)
        } else if (widthSpecMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(w, heightSpecSize)
        } else if (heightSpecMode == View.MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, h)
        }
    }

}