package com.paranid5.crescendo.core.resources.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class AppTypography(
    val h: AppHTypo,
    val body: TextStyle,
    val regular: TextStyle,
    val caption: TextStyle,
    val captionSm: TextStyle,
) {
    @Immutable
    data class AppHTypo(
        val h1: TextStyle,
        val h2: TextStyle,
        val h3: TextStyle,
    )

    companion object {
        internal val default
            get() = AppTypography(
                h = AppHTypo(
                    h1 = TextStyle(
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 32.sp,
                        lineHeight = 32.sp,
                        letterSpacing = 0.sp,
                    ),
                    h2 = TextStyle(
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        letterSpacing = 0.sp,
                    ),
                    h3 = TextStyle(
                        fontStyle = FontStyle.Normal,
                        fontWeight = FontWeight.Normal,
                        fontSize = 18.sp,
                        lineHeight = 21.sp,
                        letterSpacing = 0.sp,
                    )
                ),
                body = TextStyle(
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 20.5.sp,
                    letterSpacing = 0.sp,
                ),
                regular = TextStyle(
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    letterSpacing = 0.4.sp,
                ),
                caption = TextStyle(
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp,
                    lineHeight = 15.4.sp,
                    letterSpacing = 0.4.sp,
                ),
                captionSm = TextStyle(
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    lineHeight = 12.8.sp,
                    letterSpacing = 0.sp,
                ),
            )
    }
}

internal val LocalTypography = staticCompositionLocalOf { AppTypography.default }
