package com.paranid5.crescendo.core.resources.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Makes color lighter/darker depending on the [ratio]
 * @param ratio if < 1.0 makes color darker, if > 1.0 - lighter
 * @return updated color
 * @see <a href="https://stackoverflow.com/a/4928826">Source</a>
 */

fun Color.resetContrast(ratio: Float): Color {
    val hsv = floatArrayOf(0F, 0F, 0F)
    android.graphics.Color.colorToHSV(toArgb(), hsv)
    hsv[2] *= ratio
    return Color(android.graphics.Color.HSVToColor(hsv))
}