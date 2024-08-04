package com.paranid5.crescendo.core.resources.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.core.resources.ui.DarkPurple
import com.paranid5.crescendo.core.resources.ui.Disabled
import com.paranid5.crescendo.core.resources.ui.Emerald
import com.paranid5.crescendo.core.resources.ui.Error
import com.paranid5.crescendo.core.resources.ui.PaleCornflowerBlue
import com.paranid5.crescendo.core.resources.ui.RoyalBlue
import com.paranid5.crescendo.core.resources.ui.RoyalBlueGradient
import com.paranid5.crescendo.core.resources.ui.RussianViolet
import com.paranid5.crescendo.core.resources.ui.Tertiriary
import com.paranid5.crescendo.core.resources.ui.TransparentUtility

@Immutable
data class AppColors(
    val colorScheme: ColorScheme,
    val primary: Color,
    val secondary: Color,
    val error: Color,
    val disabled: Color,
    val background: AppBackgroundColors,
    val selection: AppSelectionColors,
    val text: AppTextColors,
    val button: AppButtonColors,
    val icon: AppIconColors,
    val utils: AppUtilsColors,
) {
    companion object {
        private val LightColorScheme = lightColorScheme(
            primary = RussianViolet,
            secondary = Emerald,
            onSecondary = PaleCornflowerBlue,
            background = RoyalBlue,
            onBackground = RoyalBlueGradient,
            inverseSurface = Color.White,
        )

        private val DarkColorScheme = darkColorScheme(
            primary = RussianViolet,
            secondary = PaleCornflowerBlue,
            onSecondary = Emerald,
            background = RoyalBlueGradient,
            onBackground = RoyalBlue,
            inverseSurface = Color.White,
        )

        internal fun create(theme: ThemeColors) = when (theme) {
            ThemeColors.Dark -> AppColors(
                colorScheme = DarkColorScheme,
                primary = RussianViolet,
                secondary = Emerald,
                error = Error,
                disabled = Disabled,
                background = AppBackgroundColors.dark,
                selection = AppSelectionColors.default,
                text = AppTextColors.default,
                button = AppButtonColors.default,
                icon = AppIconColors.default,
                utils = AppUtilsColors.default,
            )

            ThemeColors.Light -> AppColors(
                colorScheme = LightColorScheme,
                primary = RussianViolet,
                secondary = PaleCornflowerBlue,
                error = Error,
                disabled = Disabled,
                background = AppBackgroundColors.light,
                selection = AppSelectionColors.default,
                text = AppTextColors.default,
                button = AppButtonColors.default,
                icon = AppIconColors.default,
                utils = AppUtilsColors.default,
            )
        }
    }
}

@Immutable
data class AppBackgroundColors(
    val primary: Color,
    val alternative: Color,
    val gradient: Brush,
    val card: Color,
) {
    companion object {
        private val BackgroundGradientStart = Offset(x = 0F, y = Float.POSITIVE_INFINITY)
        private val BackgroundGradientEnd = Offset(x = Float.POSITIVE_INFINITY, y = 0F)

        internal val dark = AppBackgroundColors(
            primary = RoyalBlue,
            alternative = RoyalBlueGradient,
            gradient = Brush.linearGradient(
                colors = listOf(RoyalBlue, RoyalBlueGradient),
                start = BackgroundGradientStart,
                end = BackgroundGradientEnd,
            ),
            card = RussianViolet,
        )

        internal val light = AppBackgroundColors(
            primary = RoyalBlueGradient,
            alternative = RoyalBlue,
            gradient = Brush.linearGradient(
                colors = listOf(RoyalBlueGradient, RoyalBlue),
                start = BackgroundGradientStart,
                end = BackgroundGradientEnd,
            ),
            card = RussianViolet,
        )
    }
}

@Immutable
data class AppSelectionColors(
    val selected: Color,
    val notSelected: Color,
) {
    companion object {
        internal val default = AppSelectionColors(
            selected = Emerald,
            notSelected = PaleCornflowerBlue,
        )
    }
}

@Immutable
data class AppTextColors(
    val primary: Color,
    val dark: Color,
    val tertiriary: Color,
) {
    companion object {
        internal val default = AppTextColors(
            primary = Color.White,
            dark = DarkPurple,
            tertiriary = Tertiriary,
        )
    }
}

@Immutable
data class AppButtonColors(
    val primary: Color,
) {
    companion object {
        internal val default = AppButtonColors(primary = RussianViolet)
    }
}

@Immutable
data class AppIconColors(
    val primary: Color,
) {
    companion object {
        internal val default = AppIconColors(primary = DarkPurple)
    }
}

@Immutable
data class AppUtilsColors(
    val disabled: Color,
    val transparentUtility: Color,
) {
    companion object {
        internal val default = AppUtilsColors(
            disabled = Disabled,
            transparentUtility = TransparentUtility,
        )
    }
}

internal val LocalColors = compositionLocalOf { AppColors.create(ThemeColors.Dark) }
