package com.paranid5.crescendo.core.resources.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.koin.compose.KoinContext

interface Theme {
    val colorScheme: ColorScheme
    val backgroundGradient: Brush
    val itemBackgroundGradient: Brush
}

private val DarkColorTheme = object : Theme {
    override val colorScheme = darkColorScheme(
        primary = Primary,
        secondary = SecondaryAlternative,
        onSecondary = Secondary,
        background = BackgroundAlternative,
        onBackground = Background,
        inverseSurface = Color.White
    )

    override val backgroundGradient = Brush.linearGradient(
        listOf(colorScheme.background, colorScheme.onBackground.resetContrast(0.75F))
    )

    override val itemBackgroundGradient = Brush.linearGradient(
        listOf(
            colorScheme.background.resetContrast(0.5F).copy(alpha = 0.5F),
            colorScheme.onBackground.resetContrast(0.5F).copy(alpha = 0.5F)
        )
    )
}

private val LightColorTheme = object : Theme {
    override val colorScheme = lightColorScheme(
        primary = Primary,
        secondary = Secondary,
        onSecondary = SecondaryAlternative,
        background = Background,
        onBackground = BackgroundAlternative,
        inverseSurface = Color.White
    )

    override val backgroundGradient = Brush.linearGradient(
        listOf(colorScheme.background, colorScheme.onBackground.resetContrast(ratio = 1.25F))
    )

    override val itemBackgroundGradient = Brush.linearGradient(
        listOf(
            colorScheme.background.resetContrast(0.5F).copy(alpha = 0.5F),
            colorScheme.onBackground.resetContrast(0.75F).copy(alpha = 0.5F)
        )
    )
}

@JvmInline
value class AppColors(val theme: Theme = DarkColorTheme) {
    private inline val colorScheme
        get() = theme.colorScheme

    val primary
        get() = colorScheme.primary

    val secondary
        get() = colorScheme.secondary

    val secondaryAlternative
        get() = colorScheme.onSecondary

    val background
        get() = colorScheme.background

    val backgroundAlternative
        get() = colorScheme.onBackground

    val fontColor
        get() = colorScheme.inverseSurface

    val backgroundGradient
        get() = theme.backgroundGradient

    val itemBackgroundGradient
        get() = theme.itemBackgroundGradient
}

val LocalAppColors = staticCompositionLocalOf { AppColors() }

@Composable
fun CrescendoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = AppColors(if (darkTheme) DarkColorTheme else LightColorTheme)

    KoinContext {
        CompositionLocalProvider(LocalAppColors provides appColors) {
            MaterialTheme(
                colorScheme = appColors.theme.colorScheme,
                typography = Typography,
                content = content
            )
        }
    }
}