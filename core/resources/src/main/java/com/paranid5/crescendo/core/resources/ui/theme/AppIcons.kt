package com.paranid5.crescendo.core.resources.ui.theme

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import com.paranid5.crescendo.core.resources.R

@Immutable
data class AppIcons(
    @DrawableRes val audioTrackRes: Int,
) {
    companion object {
        private val dark = AppIcons(
            audioTrackRes = R.drawable.audio_track_horizontal_night_transparent,
        )

        private val light = AppIcons(
            audioTrackRes = R.drawable.audio_track_horizontal_day_transparent,
        )

        fun create(theme: ThemeColors) = when (theme) {
            is ThemeColors.Dark -> dark
            is ThemeColors.Light -> light
        }
    }
}

internal val LocalIcons = compositionLocalOf { AppIcons.create(ThemeColors.Dark) }
