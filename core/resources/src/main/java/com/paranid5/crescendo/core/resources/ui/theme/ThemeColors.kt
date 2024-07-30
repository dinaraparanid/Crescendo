package com.paranid5.crescendo.core.resources.ui.theme

sealed interface ThemeColors {
    data object Light : ThemeColors
    data object Dark : ThemeColors
}
