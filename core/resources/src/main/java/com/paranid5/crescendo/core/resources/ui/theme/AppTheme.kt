package com.paranid5.crescendo.core.resources.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import org.koin.compose.KoinContext

data object AppTheme {
    val colors @Composable get() = LocalColors.current
    val dimensions @Composable get() = LocalDimensions.current
    val typography @Composable get() = LocalTypography.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dimensions: AppDimensions = AppDimensions.default,
    typography: AppTypography = AppTypography.default,
    content: @Composable () -> Unit
) {
    val appColors = AppColors.create(if (darkTheme) ThemeColors.Dark else ThemeColors.Light)

    KoinContext {
        CompositionLocalProvider(
            LocalColors provides appColors,
            LocalDimensions provides dimensions,
            LocalTypography provides typography,
        ) {
            MaterialTheme(
                colorScheme = appColors.colorScheme,
                typography = MaterialTypography,
                content = content,
            )
        }
    }
}