package com.adrian.simplemvpframe.utils

import org.jetbrains.annotations.NotNull

//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//   ┃　　　┃   神兽保佑
//   ┃　　　┃   代码无BUG！
//   ┃　　　┗━━━┓
//   ┃　　　　　　　┣┓
//   ┃　　　　　　　┏┛
//   ┗┓┓┏━┳┓┏┛
//     ┃┫┫　┃┫┫
//     ┗┻┛　┗┻┛

/**
 * date:2020/1/8 15:37
 * author:RanQing
 * description:经典排序算法实现<https://www.cnblogs.com/guoyaohua/p/8600214.html>
 */
object SortUtils {

    /**
     * 冒泡排序
     * 时间复杂度.最佳情况：T(n) = O(n)   最差情况：T(n) = O(n^2)   平均情况：T(n) = O(n^2)
     * 空间复杂度.O(1)
     * 原理:重复地遍历要排序的数列，一次比较两个元素，如果它们的顺序错误就把它们交换过来。遍历数列的工作是重复地进行直到没有再需要交换，也就是说该数列已经排序完成
     */
    fun bubbleSort(@NotNull array: IntArray): IntArray {
        val size = array.size
        if (size <= 1) return array

        for (i in 0 until size) {
            for (j in 0 until size - 1 - i) {
                if (array[j + 1] < array[j]) {
                    val temp = array[j]
                    array[j] = array[j + 1]
                    array[j + 1] = temp
                }
            }
        }
        return array
    }

    /**
     * 选择排序
     * 时间复杂度.最佳情况：T(n) = O(n^2)  最差情况：T(n) = O(n^2)  平均情况：T(n) = O(n^2)
     * 空间复杂度.O(1)
     * 原理：首先在未排序序列中找到最小（大）元素，存放到排序序列的起始位置，然后，再从剩余未排序元素中继续寻找最小（大）元素，然后放到已排序序列的末尾
     */
    fun selectionSort(@NotNull array: IntArray): IntArray {
        val size = array.size
        if (size <= 1) return array

        for (i in 0 until size) {
            var minIndex = i
            for (j in i until size) {
                if (array[j] < array[minIndex]) {
                    minIndex = j
                }
            }
            val temp = array[i]
            array[i] = array[minIndex]
            array[minIndex] = temp
        }
        return array
    }

    /**
     * 插入排序
     * 时间复杂度.最佳情况：T(n) = O(n)   最坏情况：T(n) = O(n^2)   平均情况：T(n) = O(n^2)
     * 空间复杂度.O(1)
     * 原理:通过构建有序序列，对于未排序数据，在已排序序列中从后向前扫描，找到相应位置并插入。插入排序在实现上，通常采用in-place排序（即只需用到O(1)的额外空间的排序），
     * 因而在从后向前扫描过程中，需要反复把已排序元素逐步向后挪位，为最新元素提供插入空间。
     */
    fun insertionSort(@NotNull array: IntArray): IntArray {
        val size = array.size
        if (size <= 1) return array
        var current: Int
        for (i in 0 until size - 1) {
            current = array[i + 1]
            var preIndex = i
            while (preIndex >= 0 && current < array[preIndex]) {
                array[preIndex + 1] = array[preIndex]
                preIndex--
            }
            array[preIndex + 1] = current
        }
        return array
    }
}