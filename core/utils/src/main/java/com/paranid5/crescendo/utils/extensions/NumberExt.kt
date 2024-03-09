package com.paranid5.crescendo.utils.extensions

infix fun Int.safeDiv(value: Int) = when (value) {
    0 -> 0F
    else -> this / value.toFloat()
}

infix fun Long.safeDiv(value: Long) = when (value) {
    0L -> 0F
    else -> this / value.toFloat()
}

infix fun Float.safeDiv(value: Float) = when (value) {
    0F -> 0F
    else -> this / value
}