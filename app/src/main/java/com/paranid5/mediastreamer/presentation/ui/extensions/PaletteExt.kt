package com.paranid5.mediastreamer.presentation.ui.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors

@Composable
fun Palette?.getLightVibrantOrPrimary(): Color {
    val colors = LocalAppColors.current.value
    val primaryColorVal = colors.primary.value.toInt()

    return Color(
        this
            ?.run { getLightVibrantColor(getDominantColor(primaryColorVal)) }
            ?: primaryColorVal
    )
}