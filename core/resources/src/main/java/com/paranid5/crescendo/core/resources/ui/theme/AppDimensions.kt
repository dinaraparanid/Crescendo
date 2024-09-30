package com.paranid5.crescendo.core.resources.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppDimensions(
    val padding: Padding,
    val corners: Corners,
    val separators: Separators,
    val elevation: Elevation,
    val borders: Borders,
    val lines: Lines,
) {
    companion object {
        val default
            get() = AppDimensions(
                padding = Padding(
                    zero = 0.dp,
                    minimum = 2.dp,
                    extraSmall = 4.dp,
                    small = 8.dp,
                    medium = 12.dp,
                    extraMedium = 16.dp,
                    big = 20.dp,
                    extraBig = 24.dp,
                    large = 32.dp,
                    extraLarge = 48.dp,
                    enormous = 64.dp,
                ),
                corners = Corners(
                    minimum = 4.dp,
                    extraSmall = 8.dp,
                    small = 16.dp,
                    medium = 20.dp,
                    extraMedium = 24.dp,
                    big = 32.dp,
                ),
                separators = Separators(minimum = 1.dp),
                elevation = Elevation(extraBig = 24.dp),
                borders = Borders(
                    minimum = 2.dp,
                    extraSmall = 4.dp,
                ),
                lines = Lines(audioEffectsSlider = 4.dp),
            )
    }

    @Immutable
    data class Padding(
        val zero: Dp,
        val minimum: Dp,
        val extraSmall: Dp,
        val small: Dp,
        val medium: Dp,
        val extraMedium: Dp,
        val big: Dp,
        val extraBig: Dp,
        val large: Dp,
        val extraLarge: Dp,
        val enormous: Dp,
    )

    @Immutable
    data class Corners(
        val minimum: Dp,
        val extraSmall: Dp,
        val small: Dp,
        val medium: Dp,
        val extraMedium: Dp,
        val big: Dp,
    )

    @Immutable
    data class Separators(val minimum: Dp)

    @Immutable
    data class Elevation(val extraBig: Dp)

    @Immutable
    data class Borders(
        val minimum: Dp,
        val extraSmall: Dp,
    )

    @Immutable
    data class Lines(val audioEffectsSlider: Dp)
}

internal val LocalDimensions = staticCompositionLocalOf { AppDimensions.default }
