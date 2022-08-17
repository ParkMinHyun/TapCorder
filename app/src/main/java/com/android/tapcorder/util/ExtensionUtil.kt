package com.android.tapcorder.util

object ExtensionUtil {

    val Any.TAG: String
        get() {
            val tag = "Tapcorder-${javaClass.simpleName}"
            return if (tag.length <= 23) tag else tag.substring(0, 23)
        }

    fun Int.toMinuteFormat():String {
        val intSec: Int = this % 60
        val intMin: Int = this / 60 % 60

        val stringSec: String = if (intSec > 10) intSec.toString() else "0$intSec"
        val stringMin: String = if (intMin > 10) intMin.toString() else "0$intMin"

        return "$stringMin:$stringSec"
    }
}