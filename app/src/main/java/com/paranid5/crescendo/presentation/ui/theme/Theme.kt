package com.paranid5.crescendo.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import org.koin.compose.KoinContext

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    secondary = SecondaryAlternative,
    onSecondary = Secondary,
    background = BackgroundAlternative,
    onBackground = Background,
    inverseSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    onSecondary = SecondaryAlternative,
    background = Background,
    onBackground = BackgroundAlternative,
    inverseSurface = Color.White
)

@JvmInline
value class AppColors(val colorScheme: ColorScheme = DarkColorScheme) {
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
}

val LocalAppColors = staticCompositionLocalOf { AppColors() }

@Composable
fun MediaStreamerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val appColors = AppColors(if (darkTheme) DarkColorScheme else LightColorScheme)

    KoinContext {
        CompositionLocalProvider(LocalAppColors provides appColors) {
            MaterialTheme(
                colorScheme = appColors.colorScheme,
                typography = Typography,
                content = content
            )
        }
    }
}