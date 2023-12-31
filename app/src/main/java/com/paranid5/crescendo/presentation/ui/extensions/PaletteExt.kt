package com.paranid5.crescendo.presentation.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors

@Composable
fun Palette?.getLightMutedOrPrimary(): Color {
    val colors = LocalAppColors.current
    val primaryColorVal = colors.primary.value.toInt()

    val resultColor = this
        ?.run { getLightMutedColor(getDominantColor(primaryColorVal)) }
        ?.run(Int::increaseBrightness)
        ?: primaryColorVal.increaseBrightness()

    return Color(resultColor)
}