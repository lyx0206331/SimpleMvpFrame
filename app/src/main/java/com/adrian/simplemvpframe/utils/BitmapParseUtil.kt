package com.adrian.simplemvpframe.utils

import android.graphics.*
import android.view.View
import java.util.*

/**
 * author:RanQing
 * date:2018/9/2 0002 20:56
 * description:
 **/
object BitmapParseUtil {
    fun createShareBitmap(src: Bitmap, time: Long, desc: String, avatar: Bitmap?, usrName: String, usrLoc: String): Bitmap? {
        val bmpW = src.width
        val bmpH = src.height
        val isHor = isHorizontal(bmpW, bmpH)
        val hPadding = if (isHor) 0 else 10
        val vPadding = 10
        val bottomLableH = 36
        val bmpTopPadding = if (isHor) 88 else vPadding
        val totalW = bmpW + hPadding * 2
        val totalH = bmpH + vPadding * 2 + bottomLableH + bmpTopPadding
//        val totalH = (screenW-padding*2) * bmpH / bmpW + padding * 3 + bottomLableH

        //生成画布
        var bmp = Bitmap.createBitmap(totalW, totalH, Bitmap.Config.RGB_565)
        val canvas = Canvas(bmp)
        val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val bmpPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        //绘制背景
        bgPaint.color = Color.WHITE
        canvas.drawRect(0f, 0f, totalW.toFloat(), totalH.toFloat(), bgPaint)

        //绘制主图片
        val bmpRect = Rect(0, 0, bmpW, bmpH)
        val dstRect = Rect(hPadding, bmpTopPadding, bmpW + hPadding, bmpH + bmpTopPadding)
        canvas.drawBitmap(src, bmpRect, dstRect, bgPaint)

        //绘制日期标签
        bgPaint.color = Color.parseColor("#444444")
        val dateLableH = 56
        val dateLableW = 112
        val dateLableTop = if (isHor) 16 else 32
        val dateRect = Rect(hPadding, dateLableTop, hPadding + dateLableW, dateLableTop + dateLableH)
        canvas.drawRect(dateRect, bgPaint)
        bgPaint.color = Color.WHITE
        bgPaint.textSize = 36f
        bgPaint.typeface = Typeface.DEFAULT_BOLD
        canvas.drawText(parseNumTo2Digit(getDayOfMonth(time)), dateRect.left + 14f, dateRect.top + 52f, bgPaint)
        return bmp
    }

    private fun isHorizontal(w: Int, h: Int): Boolean {
        return w >= h
    }

    private fun getTextBounds(txt: String, paint: Paint): Rect {
        val rect = Rect()
        paint.getTextBounds(txt, 0, txt.length, rect)
        return rect
    }

    /**
     * 计算绘制文字时的基线到中轴线的距离
     */
    private fun getBaseLine(paint: Paint): Float {
        val metrics = paint.fontMetrics
        return (metrics.descent - metrics.ascent) / 2 - metrics.descent
    }

    fun parseNumTo2Digit(num: Int): String {
        return String.format("%02d", num)
    }

    /**
     * 获取对应月份日期
     *
     * @param utcTime
     * @return
     */
    fun getDayOfMonth(utcTime: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.timeInMillis = utcTime
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取对应年份
     *
     * @param utcTime
     * @return
     */
    fun getYear(utcTime: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.timeInMillis = utcTime
        return calendar.get(Calendar.YEAR)
    }

    /**
     * 获取对应月份
     *
     * @param utcTime
     * @return
     */
    fun getMonth(utcTime: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.timeInMillis = utcTime
        return calendar.get(Calendar.MONTH)
    }
}