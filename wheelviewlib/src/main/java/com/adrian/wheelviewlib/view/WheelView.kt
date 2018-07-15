package com.adrian.wheelviewlib.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.Gravity
import android.view.View
import com.adrian.wheelviewlib.R
import com.adrian.wheelviewlib.adapter.WheelAdapter
import com.adrian.wheelviewlib.interfaces.IPickerViewData
import com.adrian.wheelviewlib.listener.LoopViewGestureListener
import com.adrian.wheelviewlib.listener.OnItemSelectedListener
import com.adrian.wheelviewlib.timer.InertiaTimerTask
import com.adrian.wheelviewlib.timer.MessageHandler
import com.adrian.wheelviewlib.timer.SmoothScrollTimerTask
import java.util.*
import java.util.concurrent.*

/**
 * author:RanQing
 * date:2018/7/10 0010
 * description:
 **/
class WheelView : View {

    companion object {
        //滑行速度
        const val VELOCITY_FLING: Long = 5L

        //非中间文字位置由此控制高度，压扁形成3D错觉
        const val SCALE_CONTENT: Float = .8f

        const val DEFAULT_TEXT_TARGET_SKEWX: Float = .5f

        //偏移量
        var CENTER_CONTENT_OFFSET: Float = 0f
    }

    enum class ACTION {
        CLICK, FLING, DAGGLE
    }

    enum class DividerType {
        FILL, WRAP
    }

    //分隔线类型
    var dividerType: DividerType = DividerType.FILL

    private var mHandler: Handler = MessageHandler(this)
    private var gestureDetector: GestureDetector = GestureDetector(context, LoopViewGestureListener(this))
    var onItemSelectedListener: OnItemSelectedListener? = null

    private var isOptions: Boolean = false
    private var isCenterLabel: Boolean = true

    private var mExecutor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var mFuture: ScheduledFuture<*>? = null

    private var paintOuterText = Paint()
    private var paintCenterText = Paint()
    private var paintIndicator = Paint()

    private var adapter: WheelAdapter<Any>? = null
        set(value) {
            field = value
            remeasure()
            invalidate()
        }

    //附加单位
    var label: String? = null
    var textSize: Float = 12f
        set(value) {
            if (value > 0f) {
                field = resources.displayMetrics.density * value
                paintOuterText.textSize = field
                paintCenterText.textSize = field
            }
        }
    private var maxTextWidth: Int = 0
    private var maxTextHeight: Int = 0
    private var textXOffset: Int = 0
    //每行高度
    private var itemHeight: Float = 0f

    //字体样式。等宽字体
    var typeface: Typeface = Typeface.MONOSPACE
        set(value) {
            field = value
            paintOuterText.typeface = value
            paintCenterText.typeface = value
        }
    var textColorOut = Color.BLACK
    var textColorCenter: Int = Color.BLACK
    var dividerColor: Int = Color.BLACK

    //条目间距倍数
    private var lineSpacingMultiplier = 1.6f
    var isLoop: Boolean = false

    //第一条线Y坐标值
    private var firstLineY: Float = 0f
    //第二条线Y坐标值
    private var secondLineY: Float = 0f
    //中间label绘制的Y坐标
    private var centerY: Float = 0f

    //当前滚动总高度y值
    private var totalScrollY: Float = 0f

    //初始化默认选中项
    private var initPosition: Int = 0
    //选中的item是第几个
    private var selectedItem: Int = 0
    private var preCurrentIndex: Int = 0
    //滚动偏移值，用于记录滚动了多少个item
    private var change: Int = 0

    //绘制几个条目，实际上第一项和最后一项Y轴压缩成0%了，所以可见数目实际为9
    var itemsVisible = 11

    //控件高度
    private var measureHeight: Float = 0.0f
    //控件宽度
    private var measureWidth: Int = 0

    //半径
    var radius: Float = 0.0f

    private var mOffset: Float = 0f
    private var previousY: Float = 0f
    private var startTime: Long = 0L

    private var widthMeasureSpec: Int = 0

    private var mGravity: Int = Gravity.CENTER
    //中间选中文字开始绘制位置
    private var drawCenterContentStart: Int = 0
    //非中间文字开始绘制位置
    private var drawOutContentStart: Int = 0

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        val dm: DisplayMetrics = resources.displayMetrics
        val density: Float = dm.density //屏幕密度比(0.75/1.0/1.5/2.0/3.0)

        CENTER_CONTENT_OFFSET = if (density < 1) {
            2.4f
        } else if (density >= 1 && density < 1.5f) {
            3.6f
        } else if (density >= 1.5f && density < 2) {
            4.5f
        } else if (density >= 2 && density < 3) {
            6f
        } else {
            density * 2.5f
        }

        if (attrs != null && context != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.wheelView, defStyleAttr, 0)
            mGravity = a.getInt(R.styleable.wheelView_wv_gravity, Gravity.CENTER)
            textColorOut = a.getColor(R.styleable.wheelView_wv_textColorOut, Color.GRAY)
            textColorCenter = a.getColor(R.styleable.wheelView_wv_textColorCenter, Color.BLACK)
            dividerColor = a.getColor(R.styleable.wheelView_wv_dividerColor, Color.GRAY)
            textSize = a.getDimension(R.styleable.wheelView_wv_textSize, textSize)
            lineSpacingMultiplier = a.getFloat(R.styleable.wheelView_wv_lineSpacingMultiplier, lineSpacingMultiplier)
            a.recycle()
        }

        judgeLineSpace()
        initLoopView()
    }

    /**
     * 判断间距是否在1.0 - 4.0之间
     */
    private fun judgeLineSpace() {
        if (lineSpacingMultiplier < 1.0f) {
            lineSpacingMultiplier = 1.0f
        } else if (lineSpacingMultiplier > 4.0f) {
            lineSpacingMultiplier = 4f
        }
    }

    private fun initLoopView() {
        gestureDetector.setIsLongpressEnabled(false)
        isLoop = true

        totalScrollY = 0f
        initPosition = -1
        initPaints()
    }

    private fun initPaints() {
        paintOuterText.color = textColorOut
        paintOuterText.isAntiAlias = true
        paintOuterText.typeface = typeface
        paintOuterText.textSize = textSize

        paintCenterText.color = textColorCenter
        paintCenterText.isAntiAlias = true
        paintCenterText.typeface = typeface
        paintCenterText.textScaleX = 1.1f
        paintCenterText.textSize = textSize

        paintIndicator.color = dividerColor
        paintIndicator.isAntiAlias = true

        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    /**
     * 重新测量
     */
    private fun remeasure() {
        if (adapter == null) {
            return
        }
        measureTextWidthHeight()

        //半圆的周长 = item的高度乘以item数目减1
        val halfCircumference = itemHeight * (itemsVisible - 1)
        //整个圆的周长除以PI得到直径,为控件的总高度
        measureHeight = (halfCircumference * 2 / Math.PI).toFloat()
        //半径
        radius = (halfCircumference / Math.PI).toFloat()
        //控件宽度，支持weight
        measureWidth = MeasureSpec.getSize(widthMeasureSpec)
        //计算两条横线和选中项画笔的基线Y位置
        firstLineY = (measureHeight - itemHeight) / 2f
        secondLineY = (measureHeight + itemHeight) / 2f
        centerY = secondLineY - (itemHeight - maxTextHeight) / 2 - CENTER_CONTENT_OFFSET

        //初始化显示的item的position
        if (initPosition == -1) {
            initPosition = if (isLoop) (adapter!!.getItemCount() + 1) / 2 else 0
        }

        preCurrentIndex = initPosition
    }

    /**
     * 计算最长Text的宽高
     */
    private fun measureTextWidthHeight() {
        val rect = Rect()
        val count: Int = if (adapter == null) 0 else adapter!!.getItemCount()
        for (i in 0 until count) {
            val s = getContentText(adapter?.getItem(i))
            paintCenterText.getTextBounds(s, 0, s.length, rect)

            val textWidth = rect.width()
            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth
            }
            //星期的标准编码(以它为标准高度)
            paintCenterText.getTextBounds("\u661F\u671F", 0, 2, rect)

            maxTextHeight = rect.height() + 2
        }

        itemHeight = lineSpacingMultiplier * maxTextHeight
    }

    /**
     * 平滑滚动实现
     */
    fun smoothScroll(action: ACTION) {
        cancelFuture()
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            mOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight
            //如果超过item高度的一半，滚动到下一个item去
            mOffset = if (mOffset > itemHeight / 2) itemHeight - mOffset else -mOffset
        }
        //停止的时候，位置有偏移，不是全部都能正确停止到中间位置，这里把文字位置挪回中间去
        mFuture = mExecutor.scheduleWithFixedDelay(SmoothScrollTimerTask(this, mOffset), 0, 10, TimeUnit.MILLISECONDS)
    }

    fun scrollBy(velocityY: Float) {
        cancelFuture()
        mFuture = mExecutor.scheduleWithFixedDelay(InertiaTimerTask(this, velocityY), 0, VELOCITY_FLING, TimeUnit.MILLISECONDS)
    }

    fun cancelFuture() {
        if (mFuture != null && !mFuture!!.isCancelled) {
            mFuture!!.cancel(true)
            mFuture = null
        }
    }

    fun setCurrentItem(currentItem: Int) {
        //不添加这句,当这个wheelView不可见时,默认都是0,会导致获取到的时间错误
        selectedItem = currentItem
        initPosition = currentItem
        //回归顶部，不然重设位置会偏移，显示出不对位置的数据
        totalScrollY = 0f
        invalidate()
    }

    fun getCurrentItem(): Int {
        return if (adapter == null) 0 else Math.max(0, Math.min(selectedItem, adapter!!.getItemCount() - 1))
    }

    fun onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed({
                run {
                    onItemSelectedListener?.onItemSelected(getCurrentItem())
                }
            }, 200)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (adapter == null) {
            return
        }
    }

    /**
     * 获取所显示的数据源
     * @param item 数据源
     * @return 对应显示的字符串
     */
    private fun getContentText(item: Any?): String {
        return when (item) {
            null -> ""
            is IPickerViewData -> item.getPickerViewText()
            is Int -> //如果为整形至少保留两位整数
                String.format(Locale.getDefault(), "%02d", item)
            else -> item.toString()
        }
    }
}