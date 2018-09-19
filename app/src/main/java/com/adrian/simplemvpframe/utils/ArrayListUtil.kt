package com.adrian.simplemvpframe.utils

import android.util.Log
import com.adrian.simplemvpframe.model.TestModel
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import java.lang.reflect.Field

/**
 * date:2018/9/19 19:13
 * author：RanQing
 * description：
 */
object ArrayListUtil {

    fun <T> removeList(list: ArrayList<T>, removeItem: T) {
        for ((index, value) in list.withIndex()) {
            if (value == removeItem) {
                list.remove(value)
                return
            }
        }
    }

    fun <T> removeList(list: ArrayList<T>, removeList: ArrayList<T>) {
        val removeIndexes = ArrayList<Int>()
        for ((index, value) in list.withIndex()) {
            for (removeItem in removeList) {
                if (removeItem == value) {
                    removeIndexes.add(index)
                }
            }
        }

        for (i in removeIndexes.size - 1 downTo 0) {
            list.removeAt(i)
        }
    }

    fun <T> removeByAttrValues(@NotNull list: ArrayList<T>, attr: Any, vararg values: Any) {
        for ((index, value) in list.withIndex()) {
            if (value != null) {
//                value::class
            }
        }
    }

    fun logE(msg: String) {
        Log.e("TEST", msg)
    }

    fun test(obj: Any) {
        obj.getAllFields().map {
            logE("${it.name}")
        }
    }

    inline fun <reified T> T.getAllFields() = T::class.java.declaredFields

    inline fun <reified T> getFieldByName(t: T, fieldName: String): Field {
        val clazz = T::class.java
        return clazz.getDeclaredField(fieldName)
    }
}