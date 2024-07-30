package com.paranid5.crescendo.utils.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors

@Composable
fun Palette?.getBrightDominantOrPrimary(): Color {
    val primaryColorVal = colors.primary.value.toInt()

    val resultColor = this
        ?.run { getDominantColor(primaryColorVal) }
        ?.run(Int::increaseBrightness)
        ?: primaryColorVal.increaseBrightness()

    return Color(resultColor)
}