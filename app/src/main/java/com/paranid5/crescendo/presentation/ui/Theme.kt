package com.paranid5.crescendo.presentation.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.core.resources.ui.theme.Background
import com.paranid5.crescendo.core.resources.ui.theme.BackgroundAlternative
import com.paranid5.crescendo.core.resources.ui.theme.Primary
import com.paranid5.crescendo.core.resources.ui.theme.Secondary
import com.paranid5.crescendo.core.resources.ui.theme.SecondaryAlternative
import com.paranid5.crescendo.core.resources.ui.theme.Typography
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
fun CrescendoTheme(
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