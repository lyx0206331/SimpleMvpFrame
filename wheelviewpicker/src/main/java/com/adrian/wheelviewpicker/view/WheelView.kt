package com.adrian.wheelviewpicker.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import com.adrian.wheelviewpicker.R
import java.util.*
import java.util.Arrays.asList
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * date:2018/6/23
 * author：RanQing
 * description：
 */
class WheelView : View {

    init {
        initPaints()
    }

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
    var mGravity: Int = 0//文字显示位置
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
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
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
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.pickerView, defStyleAttr, 0)
            mGravity = a.getInt(R.styleable.pickerView_gravity, Gravity.CENTER)
            textColorOut = a.getColor(R.styleable.pickerView_textColorOut, 0xFFa8a8a8.toInt())
            textColorCenter = a.getColor(R.styleable.pickerView_textColorCenter, 0xFF2a2a2a.toInt())
            dividerColor = a.getColor(R.styleable.pickerView_dividerColor, 0xFFd5d5d5.toInt())
            centerBackground = a.getColor(R.styleable.pickerView_centerBackground, 0xFFa8a8a8.toInt())
            textSize = a.getDimensionPixelOffset(R.styleable.pickerView_textSize, sp2px(context, 16f))
            lineSpacingMultiplier = a.getFloat(R.styleable.pickerView_lineSpacingMultiplier, 2.0f)
            isLoop = a.getBoolean(R.styleable.pickerView_isLoop, false)
            initPosition = a.getInt(R.styleable.pickerView_initPosition, -1)
            visibleItemCount = a.getInt(R.styleable.pickerView_visibleItemCount, 7)
            a.recycle()//回收内存
        }

        judgeLineSpace()
        initLoopView()
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
        firstLineY = (measuredH - itemHeight) / 2.0f
        secondLineY = (measuredH + itemHeight) / 2.0f
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

    fun setItems(items: ArrayList<String>) {
        this.items = items
    }

    fun onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed(OnItemSelectedRunnable(this), 200L)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (items == null || items!!.isEmpty()) {
            return
        }

        //可见的item数组
        val visibles = arrayOfNulls<String>(visibleItemCount)
        //滚动的Y值高度除去每行Item的高度，得到滚动了多少个item，即change数
        val change = (totalScrollY / itemHeight) as Int

        //滚动中实际的预选中的item(即经过了中间位置的item) ＝ 滑动前的位置 ＋ 滑动相对位置
        preCurrentIndex = initPosition + change % items!!.size

        if (!isLoop) {//不循环的情况
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0
            }
            if (preCurrentIndex > items!!.size - 1) {
                preCurrentIndex = items!!.size - 1
            }
        } else {//循环
            if (preCurrentIndex < 0) {//举个例子：如果总数是5，preCurrentIndex ＝ －1，那么preCurrentIndex按循环来说，其实是0的上面，也就是4的位置
                preCurrentIndex += items!!.size
            }
            if (preCurrentIndex > items!!.size - 1) {//同理上面,自己脑补一下
                preCurrentIndex -= items!!.size
            }
        }
        //跟滚动流畅度有关，总滑动距离与每个item高度取余，即并不是一格格的滚动，每个item不一定滚到对应Rect里的，这个item对应格子的偏移值
        val itemHeightOffset = totalScrollY % itemHeight

        // 设置数组中每个元素的值
        var counter = 0
        while (counter < visibleItemCount) {
            var index = preCurrentIndex - (visibleItemCount / 2 - counter)//索引值，即当前在控件中间的item看作数据源的中间，计算出相对源数据源的index值
            //判断是否循环，如果是循环数据源也使用相对循环的position获取对应的item值，如果不是循环则超出数据源范围使用""空白字符串填充，在界面上形成空白无数据的item项
            when {
                isLoop -> {
                    index = getLoopMappingIndex(index)
                    visibles[counter] = items!![index]
                }
                index < 0 -> visibles[counter] = ""
                index > items!!.size - 1 -> visibles[counter] = ""
                else -> visibles[counter] = items!![index]
            }
            counter++

        }

        //绘制中间两条横线
        if (dividerType === DividerType.WRAP) {//横线长度仅包裹内容
            var startX: Float
            val endX: Float

            if (TextUtils.isEmpty(label)) {//隐藏Label的情况
                startX = (measuredW - maxTextWidth) / 2f - 12
            } else {
                startX = (measuredW - maxTextWidth) / 4f - 12
            }

            if (startX <= 0) {//如果超过了WheelView的边缘
                startX = 10f
            }
            endX = measuredW - startX
            canvas!!.drawLine(startX, firstLineY, endX, firstLineY, paintIndicator)
            canvas.drawLine(startX, secondLineY, endX, secondLineY, paintIndicator)
        } else {
            canvas!!.drawRect(0.0f, firstLineY, measuredW.toFloat(), secondLineY, paintCenterBackground)
            canvas.drawLine(0.0f, firstLineY, measuredW.toFloat(), firstLineY, paintIndicator)
            canvas.drawLine(0.0f, secondLineY, measuredW.toFloat(), secondLineY, paintIndicator)
        }

        //只显示选中项Label文字的模式，并且Label文字不为空，则进行绘制
        if (!TextUtils.isEmpty(label) && isCenterLabel) {
            //绘制文字，靠右并留出空隙
            val drawRightContentStart = measuredW - getTextWidth(paintCenterText!!, label)
            canvas.drawText(label, drawRightContentStart - centerContentOffset, centerY, paintCenterText)
        }

        counter = 0
        while (counter < visibleItemCount) {
            canvas.save()
            // 弧长 L = itemHeight * counter - itemHeightOffset
            // 求弧度 α = L / r  (弧长/半径) [0,π]
            val radian = (itemHeight * counter - itemHeightOffset) / radius
            // 弧度转换成角度(把半圆以Y轴为轴心向右转90度，使其处于第一象限及第四象限
            // angle [-90°,90°]
            val angle = (90.0 - radian / Math.PI * 180.0).toFloat()//item第一项,从90度开始，逐渐递减到 -90度

            // 计算取值可能有细微偏差，保证负90°到90°以外的不绘制
            if (angle >= 90f || angle <= -90f) {
                canvas.restore()
            } else {
                //获取内容文字
                val contentText: String

                //如果是label每项都显示的模式，并且item内容不为空、label 也不为空
                if (!isCenterLabel && !TextUtils.isEmpty(label) && !TextUtils.isEmpty(getContentText(visibles[counter]))) {
                    contentText = getContentText(visibles[counter]) + label
                } else {
                    contentText = getContentText(visibles[counter])
                }

                reMeasureTextSize(contentText)
                //计算开始绘制的位置
                measuredCenterContentStart(contentText)
                measuredOutContentStart(contentText)
                val translateY = (radius - Math.cos(radian.toDouble()) * radius - Math.sin(radian.toDouble()) * maxTextHeight / 2.0) as Float
                //根据Math.sin(radian)来更改canvas坐标系原点，然后缩放画布，使得文字高度进行缩放，形成弧形3d视觉差
                canvas.translate(0.0f, translateY)
                canvas.scale(1.0f, Math.sin(radian.toDouble()).toFloat())
                if (translateY <= firstLineY && maxTextHeight + translateY >= firstLineY) {
                    // 条目经过第一条线
                    canvas.save()
                    canvas.clipRect(0f, 0f, measuredW.toFloat(), firstLineY - translateY)
                    //canvas.scale(1.0F, (float) Math.sin(radian) * SCALE_CONTENT);
                    canvas.drawText(contentText, drawOutContentStart.toFloat(), maxTextHeight.toFloat(), paintOuterText)
                    canvas.restore()
                    canvas.save()
                    canvas.clipRect(0f, firstLineY - translateY, measuredW.toFloat(), itemHeight)
                    //canvas.scale(1.0F, (float) Math.sin(radian) * 1.0F);
                    canvas.drawText(contentText, drawCenterContentStart.toFloat(), maxTextHeight - centerContentOffset, paintCenterText)
                    canvas.restore()
                } else if (translateY <= secondLineY && maxTextHeight + translateY >= secondLineY) {
                    // 条目经过第二条线
                    canvas.save()
                    canvas.clipRect(0f, 0f, measuredW.toFloat(), secondLineY - translateY)
                    //canvas.scale(1.0F, (float) Math.sin(radian) * 1.0F);
                    canvas.drawText(contentText, drawCenterContentStart.toFloat(), maxTextHeight - centerContentOffset, paintCenterText)
                    canvas.restore()
                    canvas.save()
                    canvas.clipRect(0f, secondLineY - translateY, measuredW.toFloat(), itemHeight)
                    //canvas.scale(1.0F, (float) Math.sin(radian) * SCALE_CONTENT);
                    canvas.drawText(contentText, drawOutContentStart.toFloat(), maxTextHeight.toFloat(), paintOuterText)
                    canvas.restore()
                } else if (translateY >= firstLineY && maxTextHeight + translateY <= secondLineY) {
                    // 中间条目
                    canvas.clipRect(0, 0, measuredW, maxTextHeight)
                    //让文字居中
                    val Y = maxTextHeight - centerContentOffset//因为圆弧角换算的向下取值，导致角度稍微有点偏差，加上画笔的基线会偏上，因此需要偏移量修正一下
                    canvas.drawText(contentText, drawCenterContentStart.toFloat(), Y, paintCenterText)

                    selectedItem = items!!.indexOf(visibles[counter])

                } else {
                    // 其他条目
                    canvas.save()
                    canvas.clipRect(0, 0, measuredW, itemHeight as Int)
                    //canvas.scale(1.0F, (float) Math.sin(radian) * SCALE_CONTENT);
                    canvas.drawText(contentText, drawOutContentStart.toFloat(), maxTextHeight.toFloat(), paintOuterText)
                    canvas.restore()
                }
                canvas.restore()
                paintCenterText!!.textSize = textSize.toFloat()
            }
            counter++
        }
    }

    /**
     * 根据文字的长度 重新设置文字的大小 让其能完全显示
     * @param contentText
     */
    private fun reMeasureTextSize(contentText: String) {
        val rect = Rect()
        paintCenterText!!.getTextBounds(contentText, 0, contentText.length, rect)
        var width = rect.width()
        var size = textSize
        while (width > measuredW) {
            size--
            //设置2条横线中间的文字大小
            paintCenterText!!.textSize = size.toFloat()
            paintCenterText!!.getTextBounds(contentText, 0, contentText.length, rect)
            width = rect.width()
        }
        //设置2条横线外面的文字大小
        paintOuterText!!.textSize = size.toFloat()
    }


    //递归计算出对应的index
    private fun getLoopMappingIndex(index: Int): Int {
        var index = index
        if (index < 0) {
            index += items!!.size
            index = getLoopMappingIndex(index)
        } else if (index > items!!.size - 1) {
            index -= items!!.size
            index = getLoopMappingIndex(index)
        }
        return index
    }


    private fun measuredCenterContentStart(content: String) {
        val rect = Rect()
        paintCenterText!!.getTextBounds(content, 0, content.length, rect)
        when (mGravity) {
            Gravity.CENTER//显示内容居中
            -> drawCenterContentStart = if (TextUtils.isEmpty(label) || !isCenterLabel) {
                ((measuredW - rect.width()) * 0.5) as Int
            } else {//只显示中间label时，时间选择器内容偏左一点，留出空间绘制单位标签
                ((measuredW - rect.width()) * 0.25) as Int
            }
            Gravity.LEFT -> drawCenterContentStart = 0
            Gravity.RIGHT//添加偏移量
            -> drawCenterContentStart = measuredW - rect.width() - centerContentOffset as Int
        }
    }

    private fun measuredOutContentStart(content: String) {
        val rect = Rect()
        paintOuterText!!.getTextBounds(content, 0, content.length, rect)
        when (mGravity) {
            Gravity.CENTER -> drawOutContentStart = if (TextUtils.isEmpty(label) || !isCenterLabel) {
                ((measuredW - rect.width()) * 0.5) as Int
            } else {//只显示中间label时，时间选择器内容偏左一点，留出空间绘制单位标签
                ((measuredW - rect.width()) * 0.25) as Int
            }
            Gravity.LEFT -> drawOutContentStart = 0
            Gravity.RIGHT -> drawOutContentStart = measuredW - rect.width() - centerContentOffset as Int
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        this.widthMeasureSpec = widthMeasureSpec
        remeasure()
        setMeasuredDimension(measuredW, measuredH)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val eventConsumed = gestureDetector!!.onTouchEvent(event)
        when (event.action) {
        //按下
            MotionEvent.ACTION_DOWN -> {
                startTime = System.currentTimeMillis()
                cancelFuture()
                previousY = event.rawY
            }
        //滑动中
            MotionEvent.ACTION_MOVE -> {

                val dy = previousY - event.rawY
                previousY = event.rawY
                totalScrollY += dy

                // 边界处理。
                if (!isLoop) {
                    var top = -initPosition * itemHeight
                    var bottom = (items!!.size - 1 - initPosition) * itemHeight


                    if (totalScrollY - itemHeight * 0.25 < top) {
                        top = totalScrollY - dy
                    } else if (totalScrollY + itemHeight * 0.25 > bottom) {
                        bottom = totalScrollY - dy
                    }

                    if (totalScrollY < top) {
                        totalScrollY = top
                    } else if (totalScrollY > bottom) {
                        totalScrollY = bottom
                    }
                }
            }
        //完成滑动，手指离开屏幕
            MotionEvent.ACTION_UP -> if (!eventConsumed) {//未消费掉事件

                /**
                 * TODO<关于弧长的计算>
                 *
                 * 弧长公式： L = α*R
                 * 反余弦公式：arccos(cosα) = α
                 * 由于之前是有顺时针偏移90度，
                 * 所以实际弧度范围α2的值 ：α2 = π/2-α    （α=[0,π] α2 = [-π/2,π/2]）
                 * 根据正弦余弦转换公式 cosα = sin(π/2-α)
                 * 代入，得： cosα = sin(π/2-α) = sinα2 = (R - y) / R
                 * 所以弧长 L = arccos(cosα)*R = arccos((R - y) / R)*R
                </关于弧长的计算> */

                val y = event.y
                val L = Math.acos(((radius - y) / radius).toDouble()) * radius
                //item0 有一半是在不可见区域，所以需要加上 itemHeight / 2
                val circlePosition = ((L + itemHeight / 2) / itemHeight) as Int
                val extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight
                //已滑动的弧长值
                mOffset = ((circlePosition - visibleItemCount / 2) * itemHeight - extraOffset) as Int

                if (System.currentTimeMillis() - startTime > 120) {
                    // 处理拖拽事件
                    smoothScroll(ACTION.DAGGLE)
                } else {
                    // 处理条目点击事件
                    smoothScroll(ACTION.CLICK)
                }
            }

            else -> if (!eventConsumed) {
                val y = event.y
                val L = Math.acos(((radius - y) / radius).toDouble()) * radius
                val circlePosition = ((L + itemHeight / 2) / itemHeight) as Int
                val extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight
                mOffset = ((circlePosition - visibleItemCount / 2) * itemHeight - extraOffset) as Int
                if (System.currentTimeMillis() - startTime > 120) {
                    smoothScroll(ACTION.DAGGLE)
                } else {
                    smoothScroll(ACTION.CLICK)
                }
            }
        }

        invalidate()
        return true
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
        val fontScale = context!!.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

}