package com.adrian.heartanimation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.support.annotation.IntDef
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Checkable


/**
 * date:2018/9/11 19:52
 * author：RanQing
 * description：
 */
class HeartView : View, Checkable {

    companion object {
        const val NORMAL = 0
        const val SHRINK = 1
        const val BROKEN = 2

        //贝塞尔曲线画圆的近似常数
        private const val c = 0.551915024494f

        //心形默认选中颜色
        private const val CHECKED_COLOR = 0xe53a42
        //心形默认未选中颜色
        private const val DEFAULT_COLOR = 0x657487
        //环绕圆点的颜色
        private val dotColors = intArrayOf(0xdaa9fa, 0xf2bf4b, 0xe3bca6, 0x329aed,
                0xb1eb99, 0x67c9ad, 0xde6bac)

        //绘制心形并伴随缩小和颜色渐变
        private const val HEART_VIEW = 0
        //绘制圆形并伴随放大和颜色渐变
        private const val CIRCLE_VIEW = 1
        //绘制圆环并伴随放大和颜色渐变
        private const val RING_VIEW = 2
        //圆环减消失、心形放大、周围环绕十四圆点
        private const val RING_DOT_HEART_VIEW = 3
        //环绕的十四圆点向外移动并缩小、透明度渐变、渐隐
        private const val DOT_HEART_VIEW = 4
        //心碎
        private const val BROKEN_VIEW = 5
        //心形逐渐缩小、消失
        private const val SHRINK_VIEW = 6
    }

    private var mDefaultColor = DEFAULT_COLOR
    private var mCheckedColor = CHECKED_COLOR
    private var mUnLikeType = 0

    private var mPaintBrokenLine = Paint(Paint.ANTI_ALIAS_FLAG)

    //圆半径
    var mRadius = 100f
    //循环时间,View变化时用
    var mCycleTime = 1000L
    //是否点赞
    var mIsChecked = false

    private var mCenterX = 0f
    private var mCenterY = 0f
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mOffset = 0f
    private var mAnimatorTime: ValueAnimator? = null
    private var mBrokenTime: ValueAnimator? = null
    private var mAnimatorArgb: ValueAnimator? = null
    private var mCurrentRadius = 100f
    private var mCurrentColor = Color.WHITE
    @ViewType
    var mCurrentState = HEART_VIEW
    private var mCurrentPercent = 1f

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(HEART_VIEW, CIRCLE_VIEW, RING_VIEW, RING_DOT_HEART_VIEW, DOT_HEART_VIEW, BROKEN_VIEW, SHRINK_VIEW)
    annotation class ViewType

    private var tPointA: PointF? = null
    private var tPointB: PointF? = null
    private var tPointC: PointF? = null
    private var rPointA: PointF? = null
    private var rPointB: PointF? = null
    private var rPointC: PointF? = null
    private var bPointA: PointF? = null
    private var bPointB: PointF? = null
    private var bPointC: PointF? = null
    private var lPointA: PointF? = null
    private var lPointB: PointF? = null
    private var lPointC: PointF? = null

    private var mBrokenPointA: PointF? = null
    private var mBrokenPointB: PointF? = null
    private var mBrokenPointC: PointF? = null
    private var mBrokenPointD: PointF? = null
    private var mBrokenPointE: PointF? = null
    private var mBrokenPointF: PointF? = null
    private var mBrokenPointG: PointF? = null
    private var mBrokenPointH: PointF? = null
    private var rDotL: Float = 0f
    private var rDotS: Float = 0f
    private var offS: Float = 0f
    private var offL: Float = 0f
    private var isMax: Boolean = false
    private var dotR: Float = 0f

    private var mAnimatedBrokenValue = 0

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (context == null) return
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.PraiseView, defStyleAttr, 0)
        mRadius = ta.getDimension(R.styleable.PraiseView_cirRadius, dp2px(10f))
        mCycleTime = ta.getInt(R.styleable.PraiseView_durationTime, 600).toLong()
        mDefaultColor = ta.getColor(R.styleable.PraiseView_defaultColor, DEFAULT_COLOR)
        mCheckedColor = ta.getColor(R.styleable.PraiseView_checkedColor, CHECKED_COLOR)
        mUnLikeType = ta.getInt(R.styleable.LikeView_unlike_style, NORMAL)
        ta.recycle()
        mOffset = c * mRadius
        mCenterX = mRadius
        mCenterY = mRadius
        mPaint = Paint()
        mCurrentRadius = mRadius
        mCurrentColor = mDefaultColor
        dotR = mRadius / 6
        mPaintBrokenLine = Paint()
        mPaintBrokenLine.isAntiAlias = true
        mPaintBrokenLine.style = Paint.Style.STROKE
    }

    private fun dp2px(value: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources
                .displayMetrics)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(mCenterX, mCenterY)//使坐标原点在canvas中心位置
        when (mCurrentState) {
            HEART_VIEW -> drawHeart(canvas, mCurrentRadius, mCurrentColor)
            CIRCLE_VIEW -> drawCircle(canvas, mCurrentRadius, mCurrentColor)
            RING_VIEW -> drawRing(canvas, mCurrentRadius, mCurrentColor, mCurrentPercent)
            RING_DOT_HEART_VIEW -> drawDotWithRing(canvas, mCurrentRadius, mCurrentColor)
            DOT_HEART_VIEW -> drawDot(canvas)
            SHRINK_VIEW -> drawShrink(canvas)
            BROKEN_VIEW -> drawBroken(canvas)
        }
    }

    /**
     * 绘制心形
     */
    private fun drawHeart(canvas: Canvas, radius: Float, color: Int) {
        initControlPoints(radius)
        mPaint.color = color
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        val path = Path()
        path.moveTo(tPointB!!.x, tPointB!!.y)
        path.cubicTo(tPointC!!.x, tPointC!!.y, rPointA!!.x, rPointA!!.y, rPointB!!.x, rPointB!!.y)
        path.cubicTo(rPointC!!.x, rPointC!!.y, bPointC!!.x, bPointC!!.y, bPointB!!.x, bPointB!!.y)
        path.cubicTo(bPointA!!.x, bPointA!!.y, lPointC!!.x, lPointC!!.y, lPointB!!.x, lPointB!!.y)
        path.cubicTo(lPointA!!.x, lPointA!!.y, tPointA!!.x, tPointA!!.y, tPointB!!.x, tPointB!!.y)
        canvas.drawPath(path, mPaint)
    }

    /**
     * 绘制裂开的线
     */
    private fun drawBroken(canvas: Canvas) {
        if (mAnimatedBrokenValue in 1..600) {
            drawHeart(canvas, mRadius, mCheckedColor)
        } else {
            drawHeart(canvas, mRadius, mDefaultColor)
        }

        mPaintBrokenLine.strokeWidth = mRadius / 8
        mPaintBrokenLine.color = mDefaultColor

        val line = Path()
        line.moveTo(mBrokenPointA!!.x, mBrokenPointA!!.y)
        if (mAnimatedBrokenValue in 1..200) {
            line.lineTo(mBrokenPointB!!.x, mBrokenPointB!!.y)
            canvas.drawPath(line, mPaintBrokenLine)
        } else if (mAnimatedBrokenValue in 201..400) {
            line.lineTo(mBrokenPointB!!.x, mBrokenPointB!!.y)
            line.lineTo(mBrokenPointC!!.x, mBrokenPointC!!.y)
            canvas.drawPath(line, mPaintBrokenLine)
        } else if (mAnimatedBrokenValue in 401..600) {
            line.lineTo(mBrokenPointB!!.x, mBrokenPointB!!.y)
            line.lineTo(mBrokenPointC!!.x, mBrokenPointC!!.y)
            line.lineTo(mBrokenPointD!!.x, mBrokenPointD!!.y)
            canvas.drawPath(line, mPaintBrokenLine)
        } else if (mAnimatedBrokenValue in 601..900) {
            drawBrokenHeart(canvas)
        } else if (mAnimatedBrokenValue in 901..1099) {
            mPaint.color = mCheckedColor
            canvas.drawCircle(mBrokenPointF!!.x, mBrokenPointF!!.y, mRadius / 4, mPaint)
            canvas.drawCircle(mBrokenPointE!!.x, mBrokenPointE!!.y, mRadius / 4, mPaint)
        } else if (mAnimatedBrokenValue in 1100..1199) {
            mPaint.color = mCheckedColor
            canvas.drawCircle(mBrokenPointG!!.x, mBrokenPointG!!.y, mRadius / 6, mPaint)
            canvas.drawCircle(mBrokenPointH!!.x, mBrokenPointH!!.y, mRadius / 6, mPaint)
        }

    }

    /**
     * 绘制两边裂开的心形
     */
    private fun drawBrokenHeart(canvas: Canvas) {
        val Offset: Float
        if (mAnimatedBrokenValue in 601..750) {
            Offset = mRadius * 0.2f
            val rightPath = Path()
            mPaint.color = mCheckedColor
            rightPath.moveTo(tPointB!!.x + Offset, tPointB!!.y)
            rightPath.cubicTo(tPointC!!.x + Offset, tPointC!!.y, rPointA!!.x + Offset, rPointA!!.y,
                    rPointB!!.x + Offset, rPointB!!.y)
            rightPath.cubicTo(rPointC!!.x + Offset, rPointC!!.y, bPointC!!.x + Offset, bPointC!!.y,
                    bPointB!!.x, bPointB!!.y)
            rightPath.lineTo(-mRadius * 0.1f, mRadius * 0.3f)
            rightPath.lineTo(mRadius * 0.4f, mRadius * 0.1f)
            canvas.drawPath(rightPath, mPaint)

            val leftPath = Path()
            leftPath.moveTo(0f, -mRadius * 0.25f)
            leftPath.cubicTo(tPointA!!.x - Offset, tPointA!!.y, lPointA!!.x - Offset, lPointA!!.y,
                    lPointB!!.x - Offset, lPointB!!.y)
            leftPath.cubicTo(lPointC!!.x - Offset, lPointC!!.y, bPointA!!.x - Offset, bPointA!!.y,
                    bPointB!!.x, bPointB!!.y)
            leftPath.lineTo(-mRadius * 0.3f, mRadius * 0.3f)
            leftPath.lineTo(mRadius * 0.1f, 0f)
            canvas.drawPath(leftPath, mPaint)
        } else {
            Offset = mRadius * 0.3f
            val rightPath = Path()
            mPaint.color = mCheckedColor
            rightPath.moveTo(tPointB!!.x + Offset, tPointB!!.y)
            rightPath.cubicTo(tPointC!!.x + Offset, tPointC!!.y + mRadius * 0.1f, rPointA!!.x + Offset,
                    rPointA!!.y, rPointB!!.x + Offset, rPointB!!.y)
            rightPath.cubicTo(rPointC!!.x + Offset, rPointC!!.y, bPointC!!.x + Offset, bPointC!!.y,
                    bPointB!!.x, bPointB!!.y)
            rightPath.lineTo(0f, mRadius * 0.35f)
            rightPath.lineTo(mRadius * 0.5f, mRadius * 0.15f)
            canvas.drawPath(rightPath, mPaint)

            val leftPath = Path()
            leftPath.moveTo(-0.2f, -mRadius * 0.25f)
            leftPath.cubicTo(tPointA!!.x - Offset, tPointA!!.y + mRadius * 0.1f, lPointA!!.x - Offset,
                    lPointA!!.y, lPointB!!.x - Offset, lPointB!!.y)
            leftPath.cubicTo(lPointC!!.x - Offset, lPointC!!.y, bPointA!!.x - Offset, bPointA!!.y,
                    bPointB!!.x, bPointB!!.y)
            leftPath.lineTo(-mRadius * 0.45f, mRadius * 0.35f)
            leftPath.lineTo(-mRadius * 0.15f, 0f)
            canvas.drawPath(leftPath, mPaint)
        }

    }

    /**
     * 绘制心形
     */
    private fun drawShrink(canvas: Canvas) {
        initControlPoints(mRadius)
        mPaintBrokenLine.style = Paint.Style.STROKE
        mPaint.color = mDefaultColor
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        val path = Path()
        path.moveTo(tPointB!!.x, tPointB!!.y)
        path.cubicTo(tPointC!!.x, tPointC!!.y, rPointA!!.x, rPointA!!.y, rPointB!!.x, rPointB!!.y)
        path.cubicTo(rPointC!!.x, rPointC!!.y, bPointC!!.x, bPointC!!.y, bPointB!!.x, bPointB!!.y)
        path.cubicTo(bPointA!!.x, bPointA!!.y, lPointC!!.x, lPointC!!.y, lPointB!!.x, lPointB!!.y)
        path.cubicTo(lPointA!!.x, lPointA!!.y, tPointA!!.x, tPointA!!.y, tPointB!!.x, tPointB!!.y)
        canvas.drawPath(path, mPaint)
        if (mAnimatedBrokenValue in 1..1199) {
            canvas.rotate(Math.PI.toFloat() * (1200 - mAnimatedBrokenValue) * 90 / 1200)
            initControlPoints(mRadius * (1200 - mAnimatedBrokenValue) / 1200)
            mPaintBrokenLine.style = Paint.Style.STROKE
            mPaint.color = mCheckedColor
            mPaint.isAntiAlias = true
            mPaint.style = Paint.Style.FILL
            val path2 = Path()
            path2.moveTo(tPointB!!.x, tPointB!!.y)
            path2.cubicTo(tPointC!!.x, tPointC!!.y, rPointA!!.x, rPointA!!.y, rPointB!!.x, rPointB!!.y)
            path2.cubicTo(rPointC!!.x, rPointC!!.y, bPointC!!.x, bPointC!!.y, bPointB!!.x, bPointB!!.y)
            path2.cubicTo(bPointA!!.x, bPointA!!.y, lPointC!!.x, lPointC!!.y, lPointB!!.x, lPointB!!.y)
            path2.cubicTo(lPointA!!.x, lPointA!!.y, tPointA!!.x, tPointA!!.y, tPointB!!.x, tPointB!!.y)
            canvas.drawPath(path2, mPaint)
        }
    }

    /**
     * 绘制圆形
     */
    private fun drawCircle(canvas: Canvas, radius: Float, color: Int) {
        mPaint.color = color
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        canvas.drawCircle(0f, 0f, radius, mPaint)
    }

    /**
     * 绘制圆环
     */
    private fun drawRing(canvas: Canvas, radius: Float, color: Int, percent: Float) {

        mPaint.color = color
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 2 * mRadius * percent
        val rectF = RectF(-radius, -radius, radius, radius)
        canvas.drawArc(rectF, 0f, 360f, false, mPaint)
    }

    /**
     * 绘制圆点、圆环、心形
     */
    private fun drawDotWithRing(canvas: Canvas, radius: Float, color: Int) {
        mPaint.color = color
        mPaint.isAntiAlias = true

        mPaint.style = Paint.Style.STROKE
        if (mCurrentPercent <= 1) {
            val rectF = RectF(-radius, -radius, radius, radius)
            canvas.drawArc(rectF, 0f, 360f, false, mPaint)
        }
        mCurrentPercent = (if (1f - mCurrentPercent > 1f) 1f else 1f - mCurrentPercent) * 0.2f
        //用于计算圆环宽度，最小0，与动画进度负相关
        mPaint.strokeWidth = 2 * mRadius * mCurrentPercent

        val innerR = radius - mRadius * mCurrentPercent + dotR
        var angleA = 0.0
        var angleB = -Math.PI / 20

        offS += dotR / 17
        offL += dotR / 14
        rDotS = radius - mRadius / 12 / 2 + offS
        rDotL = innerR + offL

        mPaint.style = Paint.Style.FILL
        for (i in 0..6) {
            canvas.drawCircle((rDotS * Math.sin(angleA)) as Float, (rDotS * Math.cos(angleA)) as Float, dotR, mPaint)
            angleA += 2 * Math.PI / 7
            canvas.drawCircle((rDotL * Math.sin(angleB)) as Float, (rDotL * Math.cos(angleB)) as Float, dotR, mPaint)
            angleB += 2 * Math.PI / 7
        }
        mCurrentRadius = mRadius / 3 + offL * 4
        drawHeart(canvas, mCurrentRadius, mCheckedColor)

    }

    private fun drawDot(canvas: Canvas) {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.FILL

        var angleA = 0.0
        var angleB = -Math.PI / 20
        val dotRS: Float
        val dotRL: Float
        if (rDotL < 2.6 * mRadius) {//限制圆点的扩散范围
            rDotS += dotR / 17
            rDotL += dotR / 14
        }
        if (!isMax && mCurrentRadius <= 1.1 * mRadius) {
            offL += dotR / 14
            mCurrentRadius = mRadius / 3 + offL * 4

        } else {
            isMax = true
        }

        if (isMax && mCurrentRadius > mRadius) {
            mCurrentRadius = mCurrentRadius - dotR / 16

        }
        drawHeart(canvas, mCurrentRadius, mCheckedColor)

        mPaint.alpha = (255 * (1 - mCurrentPercent)) as Int//圆点逐渐透明
        dotRS = dotR * (1 - mCurrentPercent)
        dotRL = if (dotR * (1 - mCurrentPercent) * 4 > dotR)
            dotR
        else
            dotR * (1 - mCurrentPercent) * 3
        for (i in 0..6) {
            mPaint.color = dotColors[i]
            canvas.drawCircle((rDotS * Math.sin(angleA)).toFloat(), ((rDotS * Math.cos(angleA))).toFloat(), dotRS, mPaint)
            angleA += 2 * Math.PI / 7
            canvas.drawCircle((rDotL * Math.sin(angleB)).toFloat(), ((rDotL * Math.cos(angleB))).toFloat(), dotRL, mPaint)
            angleB += 2 * Math.PI / 7
        }

    }

    /**
     * 初始化Bézier 曲线四组控制点
     */
    private fun initControlPoints(mRadius: Float) {
        mOffset = c * mRadius

        tPointA = PointF(-mOffset, -mRadius)
        tPointB = PointF(0f, -mRadius * 0.5f)
        tPointC = PointF(mOffset, -mRadius)

        rPointA = PointF(mRadius, -mOffset)
        rPointB = PointF(mRadius, 0f)
        rPointC = PointF(mRadius * 0.9f, mOffset)

        bPointA = PointF(-mOffset, mRadius * 0.7f)
        bPointB = PointF(0f, mRadius)
        bPointC = PointF(mOffset, mRadius * 0.7f)

        lPointA = PointF(-mRadius, -mOffset)
        lPointB = PointF(-mRadius, 0f)
        lPointC = PointF(-mRadius * 0.9f, mOffset)

        mBrokenPointA = PointF(0f, -mRadius * 0.5f)
        mBrokenPointB = PointF(mRadius * 0.3f, -mRadius * 0.17f)
        mBrokenPointC = PointF(-mRadius * 0.3f, mRadius * 0.3f)
        mBrokenPointD = PointF(0f, mRadius * 0.9f)

        mBrokenPointE = PointF(mRadius * 0.3f, mRadius * 0.5f)
        mBrokenPointF = PointF(-mRadius * 0.3f, mRadius * 0.5f)
        mBrokenPointG = PointF(mRadius * 0.5f, mRadius * 1.2f)
        mBrokenPointH = PointF(-mRadius * 0.5f, mRadius * 1.2f)
    }

    /**
     * 点赞的变化效果
     */
    fun like() {
        if (mAnimatorTime != null && mAnimatorTime!!.isRunning()) {
            return
        }
        if (mBrokenTime != null && mBrokenTime!!.isRunning()) {
            return
        }
        resetState()
        mAnimatorTime = ValueAnimator.ofInt(0, 1200)
        mAnimatorTime?.duration = mCycleTime
        mAnimatorTime?.interpolator = LinearInterpolator()//需要随时间匀速变化
        mAnimatorTime?.start()
        mAnimatorTime?.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Float

            if (animatedValue == 0f) {
                if (mAnimatorArgb == null || !mAnimatorArgb!!.isRunning) {
                    mAnimatorArgb = ofArgb(mDefaultColor, -0x8b897, -0x218434)
                    mAnimatorArgb?.duration = mCycleTime * 28 / 120
                    mAnimatorArgb?.interpolator = LinearInterpolator()
                    mAnimatorArgb?.start()
                }
            } else if (animatedValue <= 100) {
                val percent = calcPercent(0f, 100f, animatedValue)
                mCurrentRadius = mRadius - mRadius * percent
                if (mAnimatorArgb != null && mAnimatorArgb!!.isRunning) {
                    mCurrentColor = mAnimatorArgb?.animatedValue as Int
                }
                mCurrentState = HEART_VIEW
                invalidate()

            } else if (animatedValue <= 280) {
                val percent = calcPercent(100f, 340f, animatedValue)//此阶段未达到最大半径
                mCurrentRadius = 2 * mRadius * percent
                if (mAnimatorArgb != null && mAnimatorArgb!!.isRunning) {
                    mCurrentColor = mAnimatorArgb?.animatedValue as Int
                }
                mCurrentState = CIRCLE_VIEW
                invalidate()
            } else if (animatedValue <= 340) {
                val percent = calcPercent(100f, 340f, animatedValue)//半径接上一阶段增加，此阶段外环半径已经最大值
                mCurrentPercent = if (1f - percent + 0.2f > 1f) 1f else 1f - percent + 0.2f
                //用于计算圆环宽度，最小0.2，与动画进度负相关
                mCurrentRadius = 2 * mRadius * percent
                if (mAnimatorArgb != null && mAnimatorArgb!!.isRunning) {
                    mCurrentColor = mAnimatorArgb?.animatedValue as Int
                }
                mCurrentState = RING_VIEW
                invalidate()
            } else if (animatedValue <= 480) {
                val percent = calcPercent(340f, 480f, animatedValue)//内环半径增大直至消亡
                mCurrentPercent = percent
                mCurrentRadius = 2 * mRadius//外环半径不再改变
                mCurrentState = RING_DOT_HEART_VIEW
                invalidate()
            } else if (animatedValue < 1200) {
                val percent = calcPercent(480f, 1200f, animatedValue)
                mCurrentPercent = percent
                mCurrentState = DOT_HEART_VIEW
                invalidate()

            } else if (animatedValue == 1200f) {
                mCurrentColor = mCheckedColor
                mCurrentRadius = mRadius
                mCurrentState = HEART_VIEW
                mAnimatorTime?.cancel()
                mAnimatorTime?.removeAllListeners()
                invalidate()

            }
        }


    }

    /**
     * 取消点赞的变化效果
     */
    fun unLike() {

        if (mBrokenTime != null && mBrokenTime!!.isRunning) {
            return
        }
        if (mAnimatorTime != null && mAnimatorTime!!.isRunning) {
            return
        }
        unLikeState()
        when {
            mUnLikeType === NORMAL -> {
                mCurrentColor = mDefaultColor
                mCurrentRadius = mRadius
                mCurrentState = HEART_VIEW
                invalidate()
                return
            }
            mUnLikeType === BROKEN -> mCurrentState = BROKEN_VIEW
            else -> mCurrentState = SHRINK_VIEW
        }
        mBrokenTime = ValueAnimator.ofInt(0, 1200)
        mBrokenTime?.duration = mCycleTime
        mBrokenTime?.interpolator = LinearInterpolator()//需要随时间匀速变化
        mBrokenTime?.start()
        mBrokenTime?.addUpdateListener { animation ->
            mAnimatedBrokenValue = animation.animatedValue as Int
            if (mAnimatedBrokenValue === 1200) {
                mBrokenTime?.cancel()
                mBrokenTime?.removeAllListeners()
            }
            invalidate()
        }


    }

    /**
     * 重置为初始状态
     */
    private fun resetState() {
        mCurrentPercent = 0f
        mCurrentRadius = 0f
        isMax = false
        rDotS = 0f
        rDotL = 0f
        offS = 0f
        offL = 0f
        isChecked = true
    }

    /**
     * 重置为取消状态
     */
    private fun unLikeState() {
        mAnimatedBrokenValue = 0
        mCurrentPercent = 0f
        mCurrentRadius = 0f
        isMax = false
        rDotS = 0f
        rDotL = 0f
        offS = 0f
        offL = 0f
        isChecked = false
    }

    private fun calcPercent(start: Float, end: Float, current: Float): Float {
        return (current - start) / (end - start)
    }


    /**
     * @return 由于颜色变化的动画API是SDK21 添加的，这里导入了源码的 ArgbEvaluator
     */
    private fun ofArgb(vararg values: Int): ValueAnimator {
        val anim = ValueAnimator()
        anim.setIntValues(*values)
        anim.setEvaluator(ArgbEvaluator)
        return anim
    }

    /**
     * 选择/取消选择
     */
    fun selectLike(isSetChecked: Boolean) {
        if (mAnimatorTime != null && mAnimatorTime!!.isRunning) {
            return
        }
        if (isSetChecked) {
            mCurrentColor = mCheckedColor
            isChecked = true
        } else {
            mCurrentColor = mDefaultColor
            isChecked = false
        }
        mCurrentRadius = mRadius
        mCurrentState = HEART_VIEW
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mAnimatorTime?.removeAllListeners();
        mAnimatorArgb?.removeAllListeners();
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = w / 2f
        mCenterY = h / 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mWidth: Int = (5.2 * mRadius + 2 * dotR).toInt()
        val mHeight: Int = (5.2 * mRadius + 2 * dotR).toInt()
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun isChecked(): Boolean {
        return mIsChecked
    }

    override fun toggle() {
        selectLike(!mIsChecked)
    }

    override fun setChecked(p0: Boolean) {
        if (mIsChecked != p0) {
            selectLike(p0)
        }
    }
}