package com.adrian.simplemvpframe.utils

import org.jetbrains.annotations.NotNull

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
}