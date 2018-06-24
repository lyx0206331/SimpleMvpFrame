package com.adrian.simplemvpframe.views.wheel_view2

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Paint
import android.view.View
import android.graphics.Typeface
import android.os.Handler
import android.util.AttributeSet
import android.view.GestureDetector
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import android.util.DisplayMetrics
import com.adrian.simplemvpframe.R
import android.R.attr.lineSpacingMultiplier
import android.R.attr.textSize
import android.graphics.Rect
import android.view.Gravity
import com.adrian.simplemvpframe.utils.PxUtil
import java.util.*
import java.util.Arrays.asList
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * date:2018/6/23
 * author：RanQing
 * description：
 */
class WheelView : View {
    enum class ACTION { // 点击，滑翔(滑到尽头)，拖拽事件
        CLICK, FLING, DAGGLE
    }

    enum class DividerType { // 分隔线类型
        FILL, WRAP
    }

    var dividerType = DividerType.FILL//分隔线类型

    var mHandler: Handler? = null
    private var mContext: Context? = null
    private var gestureDetector: GestureDetector? = null
    var onItemSelectedListener: OnItemSelectedListener? = null

    private var mExecutor = Executors.newSingleThreadScheduledExecutor()
    var mFuture: ScheduledFuture<*>? = null

    var items: List<String>? = null
        set(value) {
            field = value
            remeasure()
            invalidate()
        }

    var textColorOut: Int = 0//未选项文字颜色
        set(value) {
            if (value != 0) {
                field = value
                paintOuterText!!.color = value
            }
        }
    var textColorCenter: Int = 0//选中项文字颜色
        set(value) {
            if (value != 0) {
                field = value
                paintCenterText!!.color = value
            }
        }
    var dividerColor: Int = 0//分割线颜色
        set(value) {
            if (value != 0) {
                field = value
                paintIndicator!!.color = value
            }
        }
    var centerBackground: Int = 0//中间背景颜色
    var textSize: Int = 0//选项的文字大小 单位为sp
        set(value) {
            if (value > 0.0f) {
                field = (mContext!!.resources.displayMetrics.density * value).toInt()
                paintOuterText!!.textSize = textSize.toFloat()
                paintCenterText!!.textSize = textSize.toFloat()
            }
        }
    var isLoop: Boolean = false//循环滚动
    var lineSpacingMultiplier: Float = 0f// 条目间距倍数 可用来设置上下间距
        set(value) {
            if (value != 0f) {
                field = value
                judgeLineSpace()
            }
        }
    var gravity: Int = 0//文字显示位置
    var initPosition: Int = 0//初始化默认选中项
    var visibleItemCount: Int = 0// 绘制几个条目
        set(value) {    //设置显示的选项个数，必须是奇数
            if (value % 2 == 0) {
                throw IllegalArgumentException("must be odd")
            }
            if (value != visibleItemCount) {
                field = value
            }
        }

    private var paintOuterText: Paint? = null//未选项画笔
    private var paintCenterText: Paint? = null//选中项画笔
    private var paintIndicator: Paint? = null//分割线画笔
    private var paintCenterBackground: Paint? = null//选中背景画笔
    var isCenterLabel: Boolean = true//附加单位是否仅仅只显示在选中项后面
    var label: String? = null//附加单位
    var maxTextWidth: Int = 0//最大的文字宽
    var maxTextHeight: Int = 0//最大的文字高
    var itemHeight: Float = 0f//每行高度
    var typeface = Typeface.MONOSPACE!!//字体样式，默认是等宽字体
        set(value) {
            field = value
            paintOuterText!!.typeface = typeface
            paintCenterText!!.typeface = typeface
        }
    var firstLineY: Float = 0f// 第一条线Y坐标值
    var secondLineY: Float = 0f//第二条线Y坐标
    var centerY: Float = 0f//中间label绘制的Y坐标
    var totalScrollY = 0f//滚动总高度y值
    var selectedItem: Int = 0//选中的Item是第几个
    var preCurrentIndex: Int = 0
    var measuredH: Int = 0// WheelView 控件高度
    var measuredW: Int = 0// WheelView 控件宽度
    // 半径
    var radius = 0
    private var mOffset = 0
    private var previousY = 0f
    var startTime: Long = 0
    private var VELOCITY_FLING = 5// 修改这个值可以改变滑行速度
    var widthMeasureSpec: Int = 0

    private var drawCenterContentStart = 0//中间选中文字开始绘制位置
    private var drawOutContentStart = 0//非中间文字开始绘制位置
    private var SCALE_CONTENT = 0.8f//非中间文字则用此控制高度，压扁形成3d错觉
    private var centerContentOffset: Float = 0.toFloat()//偏移量

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val dm = resources.displayMetrics
        val density = dm.density // 屏幕密度（0.75/1.0/1.5/2.0/3.0）

        if (density < 1) {//根据密度不同进行适配
            centerContentOffset = 2.4f
        } else if (1 <= density && density < 2) {
            centerContentOffset = 3.6f
        } else if (1 <= density && density < 2) {
            centerContentOffset = 4.5f
        } else if (2 <= density && density < 3) {
            centerContentOffset = 6.0f
        } else if (density >= 3) {
            centerContentOffset = density * 2.5f
        }

        if (attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.pickerView, 0, 0)
            gravity = a.getInt(R.styleable.pickerView_gravity, Gravity.CENTER)
            textColorOut = a.getColor(R.styleable.pickerView_textColorOut, -0x575758)
            textColorCenter = a.getColor(R.styleable.pickerView_textColorCenter, -0xd5d5d6)
            dividerColor = a.getColor(R.styleable.pickerView_dividerColor, -0x2a2a2b)
            centerBackground = a.getColor(R.styleable.pickerView_centerBackground, -0x575758)
            textSize = a.getDimensionPixelOffset(R.styleable.pickerView_textSize, 16)
            lineSpacingMultiplier = a.getFloat(R.styleable.pickerView_lineSpacingMultiplier, 2.0f)
            isLoop = a.getBoolean(R.styleable.pickerView_isLoop, false)
            initPosition = a.getInt(R.styleable.pickerView_initPosition, -1)
            visibleItemCount = a.getInt(R.styleable.pickerView_visibleItemCount, 7)
            a.recycle()//回收内存
        }
    }

    /**
     * 判断间距是否在1.0-4.0之间
     */
    private fun judgeLineSpace() {
        if (lineSpacingMultiplier < 1.5f) {
            lineSpacingMultiplier = 1.5f
        } else if (lineSpacingMultiplier > 4.0f) {
            lineSpacingMultiplier = 4.0f
        }
    }

    private fun initLoopView() {
        mHandler = MessageHandler(this)
        gestureDetector = GestureDetector(mContext, LoopViewGestureListener(this))
        gestureDetector!!.setIsLongpressEnabled(false)

        initPaints()
    }

    private fun initPaints() {
        paintOuterText = Paint()
        paintOuterText!!.color = textColorOut
        paintOuterText!!.isAntiAlias = true
        paintOuterText!!.typeface = typeface
        paintOuterText!!.textSize = textSize.toFloat()

        paintCenterText = Paint()
        paintCenterText!!.color = textColorCenter
        paintCenterText!!.isAntiAlias = true
//        paintCenterText!!.textScaleX = 1.1F;
        paintCenterText!!.typeface = typeface
        paintCenterText!!.textSize = textSize.toFloat()

        paintIndicator = Paint()
        paintIndicator!!.color = dividerColor
        paintIndicator!!.isAntiAlias = true

        paintCenterBackground = Paint()
        paintCenterBackground!!.color = centerBackground
        paintCenterBackground!!.isAntiAlias = true

        if (android.os.Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }

    /**
     * 重新测量
     */
    private fun remeasure() {
        if (items == null) {
            return
        }

        measureTextWidthHeight()

        //半圆的周长 = item高度乘以item数目-1
        val halfCircumference = (itemHeight * (visibleItemCount - 1)).toInt()
        //整个圆的周长除以PI得到直径，这个直径用作控件的总高度
        measuredH = (halfCircumference * 2 / Math.PI).toInt()
        //求出半径
        radius = (halfCircumference / Math.PI).toInt()
        //控件宽度，这里支持weight
        measuredW = View.MeasureSpec.getSize(widthMeasureSpec)
        //计算两条横线 和 选中项画笔的基线Y位置
        firstLineY = (measuredHeight - itemHeight) / 2.0f
        secondLineY = (measuredHeight + itemHeight) / 2.0f
        centerY = secondLineY - (itemHeight - maxTextHeight) / 2.0f - centerContentOffset

        //初始化显示的item的position
        if (initPosition == -1) {
            initPosition = if (isLoop) {
                (items!!.size + 1) / 2
            } else {
                0
            }
        }
        preCurrentIndex = initPosition
    }

    /**
     * 计算最大length的Text的宽高度
     */
    private fun measureTextWidthHeight() {
        val rect = Rect()
        for (i in items!!.indices) {
            val s1 = getContentText(items!![i])
            paintCenterText!!.getTextBounds(s1, 0, s1.length, rect)

            val textWidth = rect.width()

            if (textWidth > maxTextWidth) {
                maxTextWidth = textWidth
            }
            paintCenterText!!.getTextBounds("\u661F\u671F", 0, 2, rect) // 星期的字符编码（以它为标准高度）

            maxTextHeight = rect.height() + 2

        }
        itemHeight = lineSpacingMultiplier * maxTextHeight
    }

    /**
     * @param item 数据源的item
     * @return 对应显示的字符串
     */
    private fun getContentText(item: Any?): String {
        if (item == null) {
            return ""
        } else if (item is Int) {
            //如果为整形则最少保留两位数.
            return String.format(Locale.getDefault(), "%02d", item)
        }
        return item.toString()
    }

    internal fun smoothScroll(action: ACTION) {//平滑滚动的实现
        cancelFuture()
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            mOffset = ((totalScrollY % itemHeight + itemHeight) % itemHeight).toInt()
            mOffset = if (mOffset.toFloat() > itemHeight / 2.0f) {//如果超过Item高度的一半，滚动到下一个Item去
                (itemHeight - mOffset.toFloat()).toInt()
            } else {
                -mOffset
            }
        }
        //停止的时候，位置有偏移，不是全部都能正确停止到中间位置的，这里把文字位置挪回中间去
        mFuture = mExecutor.scheduleWithFixedDelay(SmoothScrollTimerTask(this, mOffset), 0, 10, TimeUnit.MILLISECONDS)
    }

    internal fun scrollBy(velocityY: Float) {//滚动惯性的实现
        cancelFuture()
        mFuture = mExecutor.scheduleWithFixedDelay(InertiaTimerTask(this, velocityY), 0, VELOCITY_FLING.toLong(), TimeUnit.MILLISECONDS)
    }

    fun cancelFuture() {
        if (mFuture != null && !mFuture!!.isCancelled) {
            mFuture!!.cancel(true)
            mFuture = null
        }
    }

    fun setTextSize(size: Float) {
        if (size > 0.0f) {
            textSize = (context.resources.displayMetrics.density * size).toInt()
            paintOuterText!!.textSize = textSize.toFloat()
            paintCenterText!!.textSize = textSize.toFloat()
        }
    }

    fun setCurrentItem(position: Int) {
        this.initPosition = position
        totalScrollY = 0f//回归顶部，不然重设setCurrentItem的话位置会偏移的，就会显示出不对位置的数据
        invalidate()

        if (items == null || items!!.isEmpty()) {
            return
        }
        val size = items!!.size
        if (position in 0..(size - 1) && position != selectedItem) {
            initPosition = position
            totalScrollY = 0f
            mOffset = 0
            invalidate()
        }

    }

    fun setItems(items: Array<String>) {
        this.items = asList(*items)
    }

    fun onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed(OnItemSelectedRunnable(this), 200L)
        }
    }

    /**
     * 获取Item个数
     *
     * @return item个数
     */
    fun getItemsCount(): Int {
        return if (items != null) items!!.size else 0
    }

    /**
     * 计算文字宽度
     */
    fun getTextWidth(paint: Paint, str: String?): Int {
        var iRet = 0
        if (str != null && str.isNotEmpty()) {
            val len = str.length
            val widths = FloatArray(len)
            paint.getTextWidths(str, widths)
            for (j in 0 until len) {
                iRet += Math.ceil(widths[j].toDouble()).toInt()
            }
        }
        return iRet
    }

    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = mContext!!.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

}