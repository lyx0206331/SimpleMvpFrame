package com.adrian.simplemvpframe

import java.io.File

fun main(args: Array<String>) {
    val map = HashMap<Char, Int>()
    File("build.gradle").readText().toCharArray().filterNot(Char::isWhitespace).forEach {
        val count = map[it]
        if (count == null) map[it] = 1
        else map[it] = count + 1
    }

    map.forEach(::println)
}