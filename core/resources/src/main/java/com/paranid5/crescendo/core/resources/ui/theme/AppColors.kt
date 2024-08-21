package com.paranid5.crescendo.core.resources.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.core.resources.ui.AirSuperiorityBlue
import com.paranid5.crescendo.core.resources.ui.Aquamarine
import com.paranid5.crescendo.core.resources.ui.DarkPurple
import com.paranid5.crescendo.core.resources.ui.DarkSlateGray
import com.paranid5.crescendo.core.resources.ui.Disabled
import com.paranid5.crescendo.core.resources.ui.Emerald
import com.paranid5.crescendo.core.resources.ui.Error
import com.paranid5.crescendo.core.resources.ui.LightBlue
import com.paranid5.crescendo.core.resources.ui.PaleCornflowerBlue
import com.paranid5.crescendo.core.resources.ui.Pang
import com.paranid5.crescendo.core.resources.ui.RoyalBlue
import com.paranid5.crescendo.core.resources.ui.RoyalBlueGradient
import com.paranid5.crescendo.core.resources.ui.RussianViolet
import com.paranid5.crescendo.core.resources.ui.SlateGray
import com.paranid5.crescendo.core.resources.ui.TertiriaryDark
import com.paranid5.crescendo.core.resources.ui.TertiriaryLight
import com.paranid5.crescendo.core.resources.ui.TiffanyBlue
import com.paranid5.crescendo.core.resources.ui.TiffanyBlueLight
import com.paranid5.crescendo.core.resources.ui.TransparentUtilityDark
import com.paranid5.crescendo.core.resources.ui.TransparentUtilityLight

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
            primary = Aquamarine,
            secondary = DarkSlateGray,
            onSecondary = SlateGray,
            background = Pang,
            onBackground = Pang,
            inverseSurface = Color.Black,
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
                selection = AppSelectionColors.dark,
                text = AppTextColors.dark,
                button = AppButtonColors.dark,
                icon = AppIconColors.dark,
                utils = AppUtilsColors.dark,
            )

            ThemeColors.Light -> AppColors(
                colorScheme = LightColorScheme,
                primary = Aquamarine,
                secondary = DarkSlateGray,
                error = Error,
                disabled = Disabled,
                background = AppBackgroundColors.light,
                selection = AppSelectionColors.light,
                text = AppTextColors.light,
                button = AppButtonColors.light,
                icon = AppIconColors.light,
                utils = AppUtilsColors.light,
            )
        }
    }
}

@Immutable
data class AppBackgroundColors(
    val primary: Color,
    val alternative: Color,
    val gradient: Brush,
    val highContrast: Color,
    val card: Color,
    val searchBar: Color,
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
            highContrast = RussianViolet,
            card = LightBlue,
            searchBar = Color.White,
        )

        internal val light = AppBackgroundColors(
            primary = Pang,
            alternative = Pang,
            gradient = Brush.linearGradient(
                colors = listOf(Pang, Pang),
                start = BackgroundGradientStart,
                end = BackgroundGradientEnd,
            ),
            highContrast = Aquamarine,
            card = AirSuperiorityBlue,
            searchBar = Aquamarine.copy(alpha = 0.75F),
        )
    }
}

@Immutable
data class AppSelectionColors(
    val selected: Color,
    val notSelected: Color,
) {
    companion object {
        internal val dark = AppSelectionColors(
            selected = Emerald,
            notSelected = PaleCornflowerBlue,
        )

        internal val light = AppSelectionColors(
            selected = DarkSlateGray,
            notSelected = SlateGray,
        )
    }
}

@Immutable
data class AppTextColors(
    val primary: Color,
    val tertiriary: Color,
    val onHighContrast: Color,
    val onButton: Color,
    val onCard: Color,
    val onSearchBar: Color,
    val onTextField: Color,
    val onBackgroundPrimary: Color,
) {
    companion object {
        internal val dark = AppTextColors(
            primary = Color.White,
            tertiriary = TertiriaryDark,
            onHighContrast = PaleCornflowerBlue,
            onButton = PaleCornflowerBlue,
            onCard = DarkPurple,
            onSearchBar = DarkPurple,
            onTextField = PaleCornflowerBlue,
            onBackgroundPrimary = Color.Black,
        )

        internal val light = AppTextColors(
            primary = Color.Black,
            tertiriary = TertiriaryLight,
            onHighContrast = SlateGray,
            onButton = TiffanyBlue,
            onCard = TiffanyBlue,
            onSearchBar = Color.Black,
            onTextField = SlateGray,
            onBackgroundPrimary = TiffanyBlue,
        )
    }
}

@Immutable
data class AppButtonColors(
    val primary: Color,
    val disabled: Color,
    val onBackgroundPrimary: Color,
    val onBackgroundPrimaryDisabled: Color,
) {
    companion object {
        internal val dark = AppButtonColors(
            primary = RussianViolet,
            disabled = RoyalBlue,
            onBackgroundPrimary = LightBlue,
            onBackgroundPrimaryDisabled = PaleCornflowerBlue,
        )

        internal val light = AppButtonColors(
            primary = AirSuperiorityBlue,
            disabled = TiffanyBlue,
            onBackgroundPrimary = AirSuperiorityBlue,
            onBackgroundPrimaryDisabled = TiffanyBlueLight,
        )
    }
}

@Immutable
data class AppIconColors(
    val onSearchBar: Color,
    val selected: Color,
) {
    companion object {
        internal val dark = AppIconColors(
            onSearchBar = Color.Black,
            selected = Emerald,
        )

        internal val light = AppIconColors(
            onSearchBar = Color.Black,
            selected = DarkSlateGray,
        )
    }
}

@Immutable
data class AppUtilsColors(
    val disabled: Color,
    val transparentUtility: Color,
) {
    companion object {
        internal val dark = AppUtilsColors(
            disabled = Disabled,
            transparentUtility = TransparentUtilityDark,
        )

        internal val light = AppUtilsColors(
            disabled = Disabled,
            transparentUtility = TransparentUtilityLight,
        )
    }
}

internal val LocalColors = compositionLocalOf { AppColors.create(ThemeColors.Dark) }
