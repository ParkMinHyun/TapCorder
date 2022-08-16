package com.android.tabcorder.util

object ExtensionUtil {

    val Any.TAG: String
        get() {
            val tag = "TabCorder-${javaClass.simpleName}"
            return if (tag.length <= 23) tag else tag.substring(0, 23)
        }

}