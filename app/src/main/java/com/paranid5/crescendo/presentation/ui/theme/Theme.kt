package com.paranid5.crescendo.presentation.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    inverseSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Purple80,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    inverseSurface = Color.Black
)

@JvmInline
value class AppColors(val value: ColorScheme = DarkColorScheme)

val LocalAppColors = staticCompositionLocalOf { AppColors() }

@Composable
fun MediaStreamerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val appColors = AppColors(
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    )

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = appColors.value,
            typography = Typography,
            content = content
        )
    }
}