package com.example.viewpagerlib

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller

/**
 * author:RanQing
 * date:2018/7/1 0001
 * description:
 **/
class MyViewPager : ViewGroup {

    var viewArray: Array<View>? = null
        set(value) {
            field = value
            invalidate()
        }

    private val detector: GestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                scrollBy(distanceX.toInt(), 0)
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
        })
    }
    private val scroller: Scroller by lazy { Scroller(context) }

    var listener: IOnPageChangedListener? = null

    private var startX = 0
    private var startY = 0

    interface IOnPageChangedListener {
        fun onPageChanged(pos: Int)
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        viewArray?.forEach {
            addView(it)
        }
    }

    /**
     * 设置当前页面
     */
    fun setCurrentPage(pos: Int) {
        scroller.startScroll(scrollX, 0, pos * width - scrollX, 0, Math.abs(pos * width))
        invalidate()
        listener?.onPageChanged(pos)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (viewArray == null || viewArray!!.isEmpty()) {
            return
        }
        for (i in viewArray!!.indices) {
            getChildAt(i)?.layout(i * width, t, (i + 1) * height, b)
        }
    }

    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, 0)
            postInvalidate()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_DOWN -> {
                detector.onTouchEvent(ev)
                startX = ev?.x.toInt()
                startY = ev?.y.toInt()
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = ev?.x - startX
                val dy = ev?.y - startY
                if (Math.abs(dx) > Math.abs(dy)) {  //左右滑动，中断事件传递，防止子控件响应，由父控件处理
                    return true
                }
            }
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        detector.onTouchEvent(event)
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP -> {
                //滑动的距离加上屏幕的一半，除以屏幕宽度，如果你滑动距离超过了屏幕的一半，这个pos就加1
                var pos = (scrollX + width / 2) / width
                //滑到最后一张的时候，不能出边界
                if (pos >= viewArray!!.size) {
                    pos = viewArray!!.size - 1
                }
                //直接滑到指定位置,可能不太自然
//                scrollTo(pos * width, 0)
                //自然滑动，从手滑到的地方开始，滑动距离是页面宽度减去滑到的距离，时间由路程的大小来决定
//                scroller.startScroll(scrollX, 0, pos * width - scrollX, 0, Math.abs(pos * width))
//                invalidate()
                setCurrentPage(pos)
            }
        }
        return true
    }

}