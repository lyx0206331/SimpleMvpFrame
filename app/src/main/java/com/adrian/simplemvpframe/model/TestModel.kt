package com.adrian.simplemvpframe.model

/**
 * author:RanQing
 * date:2018/9/20 0020 1:53
 * description:
 **/
data class TestModel(val param0: String, var param1: Int, var param2: String = "abcde") {
    val attr0 = "asdgfva"
    var attr1 = 42
        get() = field * 2
    private var attr3 = "dddddd"
    private var attr4: String? = null

    fun method0(param0: String) {}

    fun method1(param0: String, param1: Int) {}

    fun method2(param0: String, param1: Int): Boolean {
        return true
    }
}