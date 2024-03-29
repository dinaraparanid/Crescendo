package com.paranid5.crescendo.utils.extensions

import android.graphics.Color
import androidx.annotation.ColorInt

fun @receiver:ColorInt Int.increaseBrightness(increase: Float = 0.25F): Int {
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    hsv[2] = maxOf(hsv[2] + increase, 1F)
    return Color.HSVToColor(hsv)
}

fun @receiver:ColorInt Int.decreaseBrightness(decrease: Float = 0.25F): Int {
    val hsv = FloatArray(3)
    Color.colorToHSV(this, hsv)
    hsv[2] = maxOf(hsv[2] - decrease, 0F)
    return Color.HSVToColor(hsv)
}