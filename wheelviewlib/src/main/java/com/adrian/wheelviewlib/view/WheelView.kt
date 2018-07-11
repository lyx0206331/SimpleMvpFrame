package com.adrian.wheelviewlib.view

import android.content.Context
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.View
import com.adrian.wheelviewlib.adapter.WheelAdapter
import com.adrian.wheelviewlib.listener.LoopViewGestureListener
import com.adrian.wheelviewlib.listener.OnItemSelectedListener
import com.adrian.wheelviewlib.timer.InertiaTimerTask
import com.adrian.wheelviewlib.timer.MessageHandler
import java.util.concurrent.*

/**
 * author:RanQing
 * date:2018/7/10 0010
 * description:
 **/
class WheelView : View {

    companion object {
        const val VELOCITY_FLING = 5L
    }

    enum class ACTION {
        CLICK, FLING, DAGGLE
    }

    enum class DividerType {
        FILL, WRAP
    }

    //分隔线类型
    private var dividerType: DividerType = DividerType.FILL

    private var mHandler: Handler = MessageHandler(this)
    private var gestureDetector: GestureDetector = GestureDetector(context, LoopViewGestureListener(this))
    var onItemSelectedListener: OnItemSelectedListener? = null

    private var isOptions: Boolean = false
    private var isCenterLabel: Boolean = true

    private var mExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var mFuture: ScheduledFuture<Any>? = null

    private var paintOuterText = Paint()
    private var paintCenterText = Paint()
    private var paintIndicator = Paint()

    private var adapter: WheelAdapter<Any>? = null

    //附加单位
    private var label: String? = null
    var textSize: Int = 0
    private var maxTextWidth: Int = 0
    private var maxTextHeight: Int = 0
    private var textXOffset: Int = 0
    //每行高度
    private var itemHeight: Float = 0f

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun scrollBy(velocityY: Float) {
        cancelFuture()
        mFuture = mExecutor.scheduleWithFixedDelay(InertiaTimerTask(this, velocityY), 0, VELOCITY_FLING, TimeUnit.MILLISECONDS) as ScheduledFuture<Any>?
    }

    fun cancelFuture() {
        if (mFuture != null && !mFuture!!.isCancelled) {
            mFuture!!.cancel(true)
            mFuture = null
        }
    }
}