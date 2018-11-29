package com.adrian.simplemvpframe.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import kotlin.math.max
import kotlin.math.min

/**
 * date:2018/11/29 13:41
 * author：RanQing
 * description：
 */
fun scaleBmpWithMinSize(bmp: Bitmap, minW: Int, minH: Int): Bitmap {
    Log.e("BITMAP", "scaleBmpWithMinSize=====================")

    val bmpW = bmp.width
    val bmpH = bmp.height
    Log.e("BITMAP", "src w:$bmpW h:$bmpH")

    var scaleW = 1f
    var scaleH = 1f
    if (bmpW < minW) {
        scaleW *= 1f * minW / bmpW
    }
    if (bmpH < minH) {
        scaleH *= 1f * minH / bmpH
    }
    val scale = max(scaleW, scaleH)
    Log.e("BITMAP", "scale:$scale w:$scaleW h:$scaleH")

    val matrix = Matrix()
    matrix.postScale(scale, scale)

    val dst = Bitmap.createBitmap(bmp, 0, 0, bmpW, bmpH, matrix, true)
    Log.e("BITMAP", "dst w:${dst.width} h:${dst.height}")

//    recycle(bmp)
    return dst
}

fun scaleBmpWithMaxSize(bmp: Bitmap, maxW: Int, maxH: Int): Bitmap {
    Log.e("BITMAP", "scaleBmpWithMaxSize=====================")

    val bmpW = bmp.width
    val bmpH = bmp.height
    Log.e("BITMAP", "src w:$bmpW h:$bmpH")

    var scaleW = 1f
    var scaleH = 1f
    if (bmpW > maxW) {
        scaleW *= maxW / bmpW
    }
    if (bmpH < maxH) {
        scaleH *= maxH / bmpH
    }
    val scale = min(scaleW, scaleH)
    Log.e("BITMAP", "scale:$scale w:$scaleW h:$scaleH")

    val matrix = Matrix()
    matrix.postScale(scale, scale)

    val dst = Bitmap.createBitmap(bmp, 0, 0, bmpW, bmpH, matrix, true)
    Log.e("BITMAP", "dst w:${dst.width} h:${dst.height}")

//    recycle(bmp)
    return dst
}

fun cropBitmap(bitmap: Bitmap, x: Int, y: Int, w: Int, h: Int): Bitmap {
    return Bitmap.createBitmap(bitmap, x, y, w, h, null, false)
}

fun recycle(bitmap: Bitmap) {
    if (!bitmap.isRecycled) {
        bitmap.recycle()
    }
}