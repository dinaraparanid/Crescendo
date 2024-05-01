package com.paranid5.crescendo.utils.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors

@Composable
fun Palette?.getLightMutedOrPrimary(): Color {
    val colors = LocalAppColors.current
    val primaryColorVal = colors.primary.value.toInt()

    val resultColor = this
        ?.run { getDominantColor(primaryColorVal) }
        ?.run(Int::increaseBrightness)
        ?: primaryColorVal.increaseBrightness()

    return Color(resultColor)
}

@Composable
fun Palette?.getVibrantOrBackground(): Color {
    val colors = LocalAppColors.current
    val secondaryColorVal = colors.backgroundAlternative.value.toInt()

    val resultColor = this
        ?.run { getLightVibrantColor(secondaryColorVal) }
        ?.run { increaseBrightness(increase = 0.5F) }
        ?: secondaryColorVal.increaseBrightness()

    return Color(resultColor)
}