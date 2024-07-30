package com.paranid5.crescendo.core.resources.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.core.resources.ui.Background
import com.paranid5.crescendo.core.resources.ui.BackgroundAlternative
import com.paranid5.crescendo.core.resources.ui.Disabled
import com.paranid5.crescendo.core.resources.ui.Error
import com.paranid5.crescendo.core.resources.ui.Primary
import com.paranid5.crescendo.core.resources.ui.Secondary
import com.paranid5.crescendo.core.resources.ui.SecondaryAlternative
import com.paranid5.crescendo.core.resources.ui.TransparentUtility
import com.paranid5.crescendo.core.resources.ui.resetContrast

@Immutable
data class AppColors(
    val colorScheme: ColorScheme,
    val primary: Color,
    val secondary: Color,
    val error: Color,
    val disabled: Color,
    val background: AppBackgroundColors,
    val text: AppTextColors,
    val button: AppButtonColors,
    val utils: AppUtilsColors,
) {
    companion object {
        private val LightColorScheme = lightColorScheme(
            primary = Primary,
            secondary = Secondary,
            onSecondary = SecondaryAlternative,
            background = Background,
            onBackground = BackgroundAlternative,
            inverseSurface = Color.White,
        )

        private val DarkColorScheme = darkColorScheme(
            primary = Primary,
            secondary = SecondaryAlternative,
            onSecondary = Secondary,
            background = BackgroundAlternative,
            onBackground = Background,
            inverseSurface = Color.White,
        )

        internal fun create(theme: ThemeColors) = when (theme) {
            ThemeColors.Dark -> AppColors(
                colorScheme = DarkColorScheme,
                primary = Primary,
                secondary = SecondaryAlternative,
                error = Error,
                disabled = Disabled,
                background = AppBackgroundColors.dark,
                text = AppTextColors.default,
                button = AppButtonColors.default,
                utils = AppUtilsColors.default,
            )

            ThemeColors.Light -> AppColors(
                colorScheme = LightColorScheme,
                primary = Primary,
                secondary = Secondary,
                error = Error,
                disabled = Disabled,
                background = AppBackgroundColors.light,
                text = AppTextColors.default,
                button = AppButtonColors.default,
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
    val itemGradient: Brush,
) {
    companion object {
        internal val light = AppBackgroundColors(
            primary = Background,
            alternative = BackgroundAlternative,
            gradient = Brush.linearGradient(
                listOf(Background, BackgroundAlternative.resetContrast(ratio = 1.25F))
            ),
            itemGradient = Brush.linearGradient(
                listOf(
                    Background.resetContrast(ratio = 0.5F).copy(alpha = 0.5F),
                    BackgroundAlternative.resetContrast(ratio = 0.5F).copy(alpha = 0.5F),
                )
            )
        )

        internal val dark = AppBackgroundColors(
            primary = BackgroundAlternative,
            alternative = Background,
            gradient = Brush.linearGradient(
                listOf(BackgroundAlternative, Background.resetContrast(ratio = 0.75F))
            ),
            itemGradient = Brush.linearGradient(
                listOf(
                    BackgroundAlternative.resetContrast(ratio = 0.5F).copy(alpha = 0.5F),
                    Background.resetContrast(ratio = 0.75F).copy(alpha = 0.5F),
                )
            )
        )
    }
}

@Immutable
data class AppTextColors(
    val primary: Color,
) {
    companion object {
        internal val default = AppTextColors(primary = Color.White)
    }
}

@Immutable
data class AppButtonColors(
    val primary: Color,
) {
    companion object {
        internal val default = AppButtonColors(primary = Primary)
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
