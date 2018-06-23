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
import android.view.Gravity
import com.adrian.simplemvpframe.utils.PxUtil


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

    private val dividerType = DividerType.FILL//分隔线类型

    //    var handler: Handler? = null
    private val gestureDetector: GestureDetector? = null
    var onItemSelectedListener: OnItemSelectedListener? = null

    var mExecutor = Executors.newSingleThreadScheduledExecutor()
    private val mFuture: ScheduledFuture<*>? = null

    var items: List<String>? = null

    var textColorOut: Int = 0//未选项文字颜色
    var textColorCenter: Int = 0//选中项文字颜色
    var dividerColor: Int = 0//分割线颜色
    var CenterBackground: Int = 0//中间背景颜色
    var textSize: Int = 0//选项的文字大小 单位为sp
    var isLoop: Boolean = false//循环滚动
    var lineSpacingMultiplier: Float = 0f// 条目间距倍数 可用来设置上下间距
    private var mGravity: Int = 0//文字显示位置
    var initPosition: Int = 0//初始化默认选中项
    var visibleItemCount: Int = 0// 绘制几个条目

    var paintOuterText: Paint? = null//未选项画笔
    var paintCenterText: Paint? = null//选中项画笔
    var paintIndicator: Paint? = null//分割线画笔
    var paintCenterBackground: Paint? = null//选中背景画笔
    private val isCenterLabel = true//附加单位是否仅仅只显示在选中项后面
    private val label: String? = null//附加单位
    var maxTextWidth: Int = 0//最大的文字宽
    var maxTextHeight: Int = 0//最大的文字高
    var itemHeight: Float = 0.toFloat()//每行高度
    var typeface = Typeface.MONOSPACE//字体样式，默认是等宽字体
    var firstLineY: Float = 0.toFloat()// 第一条线Y坐标值
    var secondLineY: Float = 0.toFloat()//第二条线Y坐标
    var centerY: Float = 0.toFloat()//中间label绘制的Y坐标
    var totalScrollY = 0f//滚动总高度y值
    private val selectedItem: Int = 0//选中的Item是第几个
    var preCurrentIndex: Int = 0
    var measuredH: Int = 0// WheelView 控件高度
    var measuredW: Int = 0// WheelView 控件宽度
    // 半径
    var radius = 0
    private val mOffset = 0
    private val previousY = 0f
    var startTime: Long = 0
    private val VELOCITY_FLING = 5// 修改这个值可以改变滑行速度
    var widthMeasureSpec: Int = 0

    private val drawCenterContentStart = 0//中间选中文字开始绘制位置
    private val drawOutContentStart = 0//非中间文字开始绘制位置
    private val SCALE_CONTENT = 0.8f//非中间文字则用此控制高度，压扁形成3d错觉
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
            mGravity = a.getInt(R.styleable.pickerView_gravity, Gravity.CENTER)
            textColorOut = a.getColor(R.styleable.pickerView_textColorOut, -0x575758)
            textColorCenter = a.getColor(R.styleable.pickerView_textColorCenter, -0xd5d5d6)
            dividerColor = a.getColor(R.styleable.pickerView_dividerColor, -0x2a2a2b)
//            CenterBackground = a.getColor(R.styleable.pickerView_centerBackground, -0x575758)
            textSize = a.getDimensionPixelOffset(R.styleable.pickerView_textSize, 16)
            lineSpacingMultiplier = a.getFloat(R.styleable.pickerView_lineSpacingMultiplier, 2.0f)
            isLoop = a.getBoolean(R.styleable.pickerView_isLoop, false)
            initPosition = a.getInt(R.styleable.pickerView_initPosition, -1)
            visibleItemCount = a.getInt(R.styleable.pickerView_visibleItemCount, 7)
            a.recycle()//回收内存
        }
    }
}