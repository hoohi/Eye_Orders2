package com.eyeorders.util.extension

fun String.formatMoney(): String = this.toFloat().formatMoney()

fun Double.formatMoney(): String = this.toFloat().formatMoney()
fun Double.isGreaterThanZero(): Boolean = this > 0

fun Float.formatMoney(): String = String.format("%.3f", this)