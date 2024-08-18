package com.paranid5.crescendo.core.resources.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import org.koin.compose.KoinContext

data object AppTheme {
    val colors @Composable get() = LocalColors.current
    val dimensions @Composable get() = LocalDimensions.current
    val typography @Composable get() = LocalTypography.current
    val icons @Composable get() = LocalIcons.current
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dimensions: AppDimensions = AppDimensions.default,
    typography: AppTypography = AppTypography.default,
    content: @Composable () -> Unit
) {
    val theme = remember(darkTheme) {
        if (darkTheme) ThemeColors.Dark else ThemeColors.Light
    }

    val appColors = remember(theme) { AppColors.create(theme) }
    val appIcons = remember(theme) { AppIcons.create(theme) }

    KoinContext {
        CompositionLocalProvider(
            LocalColors provides appColors,
            LocalDimensions provides dimensions,
            LocalTypography provides typography,
            LocalIcons provides appIcons,
            LocalTextSelectionColors provides AppTextSelectionColors,
        ) {
            MaterialTheme(
                colorScheme = appColors.colorScheme,
                typography = MaterialTypography,
                content = content,
            )
        }
    }
}