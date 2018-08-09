package com.adrian.transitionanimlib.expose.base

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.adrian.transitionanimlib.R
import com.adrian.transitionanimlib.bean.InfoBean
import com.adrian.transitionanimlib.listener.ExposeListener

/**
 * date:2018/8/9 12:22
 * author：RanQing
 * description：
 */
abstract class ExposeView(context: Context?) : RelativeLayout(context) {

    protected var exposePaint: Paint = Paint()
    private var exposeCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null // 画布bitmap
    var inflateBitmap: Bitmap? = null
        set(value) {
            field = value
            mExposeType = ExposeType.INFLATE
            invalidate()
        }
    private var xfermode: Xfermode? = null

    //画布宽度
    protected var screenWidth = 1080
    //画布高度
    protected var screenHeight = 1920
    private var maxExposeWidth: Int = 0
    protected var exposeWidth: Int = 0
    protected var startExposeX: Int = 0
    protected var startExposeY: Int = 0
    private var exposeSpeed = 5
    var exposeAcceleration = 7
    private var exposeColor = resources.getColor(R.color.transitionhelper_showmethod_end_color)
        set(value) = setExposeColor(value, false)
    private var useInflateExpose = false
    private var isResetExposeColor = false

    var mExposeListener: ExposeListener? = null
    private var mExposeType = ExposeType.DEFAULT
    private var exposeStatus = ExposeStatus.PENDDING

    init {
        exposePaint = Paint()
        exposePaint.style = Paint.Style.FILL
        exposePaint.isAntiAlias = true
        exposePaint.color = exposeColor

        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

        setWillNotDraw(false)
    }

    fun startExposeAnim(bean: InfoBean<Any>) {
        screenWidth = bean.windowWidth
        screenHeight = bean.windowHeight
        canvasBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_4444)
        if (canvasBitmap != null && !canvasBitmap!!.isRecycled()) {
            exposeCanvas = Canvas(canvasBitmap)
        }
        exposeStatus = ExposeStatus.SHOW
        startExposeX = bean.targetWidth / 2 + bean.targetRect.left
        startExposeY = bean.targetHeight / 2 + bean.targetRect.top - bean.statusBarHeight - bean.titleHeight
        exposeWidth = Math.min(Math.hypot(bean.targetWidth / 2.0, bean.targetHeight / 2.0).toInt() / 4, exposeSpeed)
        maxExposeWidth = Math.hypot(Math.max(startExposeX, screenWidth - startExposeX).toDouble(), Math.max(startExposeY, screenHeight - startExposeY).toDouble()).toInt()
        setBackgroundColor(resources.getColor(android.R.color.transparent))
        mExposeListener?.onExposeStart()
        mExposeType = if (inflateBitmap != null) {
            ExposeType.INFLATE
        } else {
            ExposeType.DEFAULT
        }
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        when (mExposeType) {
            ExposeType.DEFAULT -> if (exposeStatus === ExposeStatus.SHOW) {
                exposeDraw(canvas)
            } else {
                //do nothing
            }
            ExposeType.INFLATE -> if (exposeStatus === ExposeStatus.SHOW) {
                exposeDraw(canvas)
            } else if (exposeStatus !== ExposeStatus.STOP) {
                if (inflateBitmap != null && !inflateBitmap!!.isRecycled) {
                    canvas.drawBitmap(inflateBitmap, 0f, 0f, null)
                }
            }
        }
    }

    private fun exposeDraw(canvas: Canvas) {
        if (exposeWidth - exposeSpeed < maxExposeWidth) {
            mExposeListener?.onExposeProgrees((exposeWidth - exposeSpeed).toFloat() / maxExposeWidth)
            if (useInflateExpose && inflateBitmap != null) {
                exposeCanvas?.drawBitmap(inflateBitmap, null, RectF(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat()), null)
            } else {
                exposeCanvas?.drawRect(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), exposePaint)
            }
            exposePaint.xfermode = xfermode
            //exposeCanvas.drawCircle(startExposeX, startExposeY, exposeWidth, mPaint);
            animDrawing(exposeCanvas, exposePaint)
            //加速度的方式更改揭露动画速度
            exposeWidth += exposeSpeed
            exposeSpeed += exposeAcceleration
            exposePaint.xfermode = null
            if (canvasBitmap != null && !canvasBitmap!!.isRecycled) {
                canvas.drawBitmap(canvasBitmap, 0f, 0f, null)
            }
            invalidate()
        } else {
            exposeStatus = ExposeStatus.STOP
            visibility = View.GONE
            mExposeListener?.onExposeEnd()
            //recycle
            recyle()
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        var result = false
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> result = true
        }
        return result
    }

    private fun recyle() {
        canvasBitmap?.recycle()
        canvasBitmap = null
        inflateBitmap?.recycle()
        inflateBitmap = null
    }

    fun setExposeColor(exposeColor: Int, useInflate: Boolean) {
        this.useInflateExpose = useInflate
        this.isResetExposeColor = true
        if (!useInflate) {
            this.exposeColor = exposeColor
            exposePaint.color = exposeColor
        }
    }

    fun stop() {
        exposeStatus = ExposeStatus.STOP
        //recycle
        recyle()
    }

    abstract fun animDrawing(canvas: Canvas?, paint: Paint?)

}