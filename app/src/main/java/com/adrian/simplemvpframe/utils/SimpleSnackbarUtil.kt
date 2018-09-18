package com.adrian.simplemvpframe.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import com.adrian.simplemvpframe.R
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.textColor

/**
 * date:2018/9/18 11:31
 * author：RanQing
 * description：显示于某一控件下的Snackbar
 */
object SimpleSnackbarUtil {

    private var snackbar: Snackbar? = null

    private var mStatusHeight = -1

    fun setRoundCorner(radius: Float): SimpleSnackbarUtil {
        if (snackbar != null) {
            val bgDrawable = getBgDrawable(snackbar!!.view.background)
            bgDrawable?.cornerRadius = radius
            snackbar?.view?.background = bgDrawable
        }
        return this
    }

    fun setGravity(gravity: Int): SimpleSnackbarUtil {
        if (snackbar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val msgTV = snackbar!!.view.findViewById<TextView>(R.id.snackbar_text)
                msgTV.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                msgTV.gravity = gravity
            }
        }
        return this
    }

    fun setTextColor(@ColorInt color: Int): SimpleSnackbarUtil {
        if (snackbar != null) {
            snackbar!!.view.findViewById<TextView>(R.id.snackbar_text).textColor = color
        }
        return this
    }

    fun setTextSize(size: Float): SimpleSnackbarUtil {
        if (snackbar != null) {
            snackbar!!.view.findViewById<TextView>(R.id.snackbar_text).textSize = size
        }
        return this
    }

    /**
     * 此方法请先于
     */
    fun setBackgroundColor(@ColorInt color: Int): SimpleSnackbarUtil {
        if (snackbar != null) {
            val bgDrawable = getBgDrawable(snackbar!!.view.background)
            bgDrawable?.setColor(color)
            snackbar?.view?.background = bgDrawable
        }
        return this
    }

    fun setStroke(width: Int, @ColorInt color: Int): SimpleSnackbarUtil {
        if (snackbar != null) {
            val bgDrawable = getBgDrawable(snackbar!!.view.background)
            bgDrawable?.setStroke(width, color)
            snackbar?.view?.background = bgDrawable
        }
        return this
    }

    fun setDuration(duration: Int): SimpleSnackbarUtil {
        if (snackbar != null) {
            snackbar!!.duration = duration
        }
        return this
    }

    /**
     * @param dependView
     * @param msg
     */
    fun build(dependView: View, msg: String): SimpleSnackbarUtil {
        snackbar = Snackbar.make(dependView, msg, Snackbar.LENGTH_SHORT)
        snackbar?.view?.backgroundColor = Color.WHITE
        snackbar?.view?.findViewById<TextView>(R.id.snackbar_text)?.textColor = Color.BLACK
        val bgDrawable = GradientDrawable()
        bgDrawable.setColor((snackbar?.view?.background as ColorDrawable).color)
        snackbar?.view?.background = bgDrawable
        return this
    }

    /**
     * 设置Snackbar显示在指定View的下方
     * 注:暂时仅支持单行的Snackbar,因为[calculateSnackBarHeight]暂时仅支持单行Snackbar的高度计算
     * @param targetView        指定View
     * @param marginLeft        左边距
     * @param marginRight       右边距
     * @return
     */
    fun below(targetView: View, marginLeft: Int, marginRight: Int): SimpleSnackbarUtil {
        var loc = IntArray(2)
        targetView.getLocationOnScreen(loc)
        val contentViewTop = loc[1]
        var marginLeft = marginLeft
        var marginRight = marginRight
        if (snackbar != null) {
            marginLeft = if (marginLeft <= 0) 0 else marginLeft
            marginRight = if (marginRight <= 0) 0 else marginRight
            val locations = IntArray(2)
            targetView.getLocationOnScreen(locations)
            val snackbarHeight = calculateSnackBarHeight(snackbar!!)
            val screenHeight = getScreenHeight(snackbar!!.view.context)
            //必须保证指定View的底部可见 且 单行Snackbar可以完整的展示
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //为什么要'+2'? 因为在Android L(Build.VERSION_CODES.LOLLIPOP)以上,例如Button会有一定的'阴影(shadow)',阴影的大小由'高度(elevation)'决定.
                //为了在Android L以上的系统中展示的Snackbar不要覆盖targetView的阴影部分太大比例,所以人为减小2px的layout_marginBottom属性.
                if (locations[1] + targetView.height >= contentViewTop && locations[1] + targetView.height + snackbarHeight + 2 <= screenHeight) {
                    gravityFrameLayout(Gravity.BOTTOM)
                    val params = snackbar!!.view.layoutParams
                    (params as ViewGroup.MarginLayoutParams).setMargins(marginLeft, 0, marginRight, screenHeight - (locations[1] + targetView.height + snackbarHeight + 2))
                    snackbar!!.view.layoutParams = params
                }
            } else {
                if (locations[1] + targetView.height >= contentViewTop && locations[1] + targetView.height + snackbarHeight <= screenHeight) {
                    gravityFrameLayout(Gravity.BOTTOM)
                    val params = snackbar!!.view.layoutParams
                    (params as ViewGroup.MarginLayoutParams).setMargins(marginLeft, 0, marginRight, screenHeight - (locations[1] + targetView.height + snackbarHeight))
                    snackbar!!.view.layoutParams = params
                }
            }
        }
        return this
    }

    fun show() {
        if (snackbar != null) {
            snackbar!!.show()
        }
    }

    /**
     * 通过SnackBar现在的背景,获取其设置圆角值时候所需的GradientDrawable实例
     * @param backgroundOri
     * @return
     */
    private fun getBgDrawable(backgroundOri: Drawable): GradientDrawable? {
        var background: GradientDrawable? = null
        when (backgroundOri) {
            is GradientDrawable -> background = backgroundOri
            is ColorDrawable -> {
                val backgroundColor = backgroundOri.color
                background = GradientDrawable()
                background!!.setColor(backgroundColor)
            }
        }
        return background
    }

    /**
     * 设置Snackbar文字显示的位置
     * @param gravity
     */
    private fun gravityFrameLayout(gravity: Int) {
        if (snackbar != null) {
            val params = FrameLayout.LayoutParams(snackbar!!.view.layoutParams.width, snackbar!!.view.layoutParams.height)
            params.gravity = gravity
            snackbar!!.view.layoutParams = params
        }
    }

    /**
     * 计算单行的Snackbar的高度值(单位 pix)
     * @return
     */
    private fun calculateSnackBarHeight(snackbar: Snackbar): Int {
        //文字高度+paddingTop+paddingBottom : 14sp + 14dp*2
        val snackbarHeight = dp2px(snackbar.view.context, 28f) + sp2px(snackbar.view.context, 14f)
        Log.e("Jet", "直接获取MessageView高度:" + (snackbar.view.findViewById(R.id.snackbar_text) as View).height)
        return snackbarHeight
    }

    private fun dp2px(ctx: Context, dpValue: Float): Int {
        val density = ctx.resources.displayMetrics.density
        return (dpValue * density + 0.5f).toInt()
    }

    private fun sp2px(ctx: Context, spValue: Float): Int {
        val scaledDensity = ctx.resources.displayMetrics.scaledDensity
        return (spValue * scaledDensity + 0.5f).toInt()
    }

    /**
     * 获取屏幕的高度
     * @param context
     * @return
     */
    private fun getScreenHeight(context: Context): Int {
        val manager = context
                .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        return display.height
    }

    /**
     * 获得状态栏的高度
     * @param context
     * @return mStatusHeight
     */
    private fun getStatusHeight(context: Context): Int {
        if (mStatusHeight != -1) {
            return mStatusHeight
        }
        try {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                mStatusHeight = context.resources.getDimensionPixelSize(resourceId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mStatusHeight
    }
}