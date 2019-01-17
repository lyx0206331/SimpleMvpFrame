package com.adrian.simplemvpframe.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.support.annotation.IntRange
import android.util.Log
import org.jetbrains.annotations.NotNull
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
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

/**
 * JPG图片普通压缩
 */
fun compressImgNormal(src: Bitmap, dstSize: Long): Bitmap? {
    val srcSize = src.byteCount
    Log.e("BITMAP", "普通压缩前大小：$srcSize")
    val baos = ByteArrayOutputStream()
    var dst: Bitmap? = null
    //options的值是0-100，这里100表示原来图片的质量，不压缩，把压缩后的数据存放到baos中
    src.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    var options = 90
    while (baos.toByteArray().size > dstSize) {
        baos.reset()
        src.compress(Bitmap.CompressFormat.JPEG, options, baos)
        options -= if (options == 1) {
            break
        } else if (options <= 10) {
            1
        } else {
            10
        }
    }
    val bytes = baos.toByteArray()
    if (bytes.isNotEmpty()) {
        dst = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        Log.e("BITMAP", "普通压缩后大小：${dst.byteCount}")
    }
    return dst
}

/**
 * JPG图片质量压缩
 */
fun compressImgByQuality(src: Bitmap?, dstSize: Long, isRecycle: Boolean): Bitmap? {
    if (src == null || src.width == 0 || src.height == 0 || dstSize <= 0) return null
    Log.e("BITMAP", "质量压缩前大小:${src.byteCount}")

    val baos = ByteArrayOutputStream()
    src.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    var bytes: ByteArray? = null
    if (baos.size() <= dstSize) {   //原始质量不超过目标质量时，返回原始质量
        bytes = baos.toByteArray()
    } else {
        baos.reset()
        src.compress(Bitmap.CompressFormat.JPEG, 0, baos)
        if (baos.size() >= dstSize) {   //最差质量超过目标质量时，返回最差质量
            bytes = baos.toByteArray()
        } else {
            //二分法
            var start = 0
            var end = 100
            var mid = 0
            while (start < end) {
                mid = (start + end) / 2
                baos.reset()
                src.compress(Bitmap.CompressFormat.JPEG, mid, baos)
                val len = baos.size().toLong()
                if (len == dstSize) {
                    break
                } else if (len > dstSize) {
                    end = mid - 1
                } else {
                    start = mid + 1
                }
            }
            if (end == mid - 1) {
                baos.reset()
                src.compress(Bitmap.CompressFormat.JPEG, start, baos)
            }
            bytes = baos.toByteArray()
        }
    }
    if (isRecycle && !src.isRecycled) {
        src.recycle()
    }
    val dst = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    Log.e("BITMAP", "质量压缩后大小:${dst.byteCount}")
    return dst
}

/**
 * JPG图片质量压缩
 */
fun compressImgByQuality(src: Bitmap?, @IntRange(from = 0, to = 100) qualite: Int, isRecycle: Boolean): Bitmap? {
    if (src == null || src.width == 0 || src.height == 0) return null

    Log.e("BITMAP", "质量压缩前大小:${src.byteCount}")
    val baos = ByteArrayOutputStream()
    src.compress(Bitmap.CompressFormat.JPEG, qualite, baos)
    val bytes = baos.toByteArray()
    if (isRecycle && !src.isRecycled) {
        src.recycle()
    }
    val dst = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    Log.e("BITMAP", "质量压缩后大小:${dst.byteCount}")
    return dst
}