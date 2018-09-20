package com.adrian.simplemvpframe.utils

import android.util.Log
import com.adrian.simplemvpframe.model.TestModel
import org.jetbrains.annotations.NotNull
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * date:2018/9/19 19:13
 * author：RanQing
 * description：简单对象列表列表中，遍历删除包含某些指定值的对象.只处理简单对象，未对复杂数据结构作处理，有时间再优化。
 * 只对kotlin对象做处理，不适合java，java处理方式有异
 */
object SimpleOpUtil {

    /**
     * 根据列表中子对象属性名及属性值遍历删除对应数据
     * @param list 数据列表
     * @param params 参数。包含参数名及参数值，必须成对传递
     */
    fun removeByAttrValues(@NotNull list: ArrayList<*>, @NotNull vararg params: Any) {
        if (params.size % 2 != 0) {
            logE("参数不合法，传入的params参数必须是key,value形式的参数")
            return
        }

        logE("before remove")
        list.forEach {
            logE(it.toString())
        }

        var y = 0
        for (index in list.indices) {
            val value = list[index - y]
            var counter = 0
            for (i in 0..params.size - 2 step 2) {
                val k = params[i].toString()
                val v = params[i + 1]
                if (isValueExist(value, k, v)) counter++
            }
            if (counter == params.size / 2) {
                list.removeAt(index - y)
                y++
            }
        }

        logE("after remove")
        list.forEach {
            logE(it.toString())
        }
    }

    private fun logE(msg: String) {
        Log.e("TEST", msg)
    }

    /**
     * 测试方法
     */
    fun test() {
//        val obj = TestModel("aaaa", 72)
//        getMemberProp(obj).map {
//            it.isAccessible = true
//            logE("prop--${it.name}:${it.get(obj)}")
//        }
//        logE("attr3:${isMemberExist(obj, "attr3")} === attr4:${isMemberExist(obj, "attr4")}")
//        logE("obtainValue---attr4:${obtainValue(obj, "attr4")}")
//        logE("isValueExist---attr1=42:${isValueExist(obj, "attr1", 42)},attr0=aaa:${isValueExist(obj, "attr0", "aaa")}")

        val list = ArrayList<TestModel>()
        list.add(TestModel("zl", 2213, "aaaa"))
        list.add(TestModel("rq", 1231, "bbbb"))
        list.add(TestModel("sgy", 14231, "cccc"))
        list.add(TestModel("hy", 24321, "dddd"))
        list.add(TestModel("dsq", 24321, "eeee"))
        removeByAttrValues(list, "param1", 24321, "param2", "dddd")
    }

    /**
     * 判断obj对象中，成员memberName的值是否为memeberValue
     */
    fun isValueExist(obj: Any, memberName: String, memberValue: Any): Boolean {
        getMemberProp(obj).map {
            it.isAccessible = true
            if (it.name == memberName && it.get(obj) == memberValue) {
                return true
            }
        }
        return false
    }

    /**
     * 获取obj对象中对应成员memberName的值.不存在返回null
     */
    fun obtainValue(obj: Any, memberName: String): Any? {
        getMemberProp(obj).map {
            it.isAccessible = true
            if (it.name == memberName) {
                return it.get(obj)
            }
        }
        return null
    }

    /**
     * 判断obj对象内是否存在名为memberName的成员
     */
    fun isMemberExist(obj: Any, memberName: String): Boolean {
        getMemberProp(obj).map {
            it.isAccessible = true
            if (it.name == memberName) {
                return true
            }
        }
        return false
    }

    /**
     * 获取obj对象内所有成员
     */
    fun getMemberProp(obj: Any) = obj.javaClass.kotlin.memberProperties

}