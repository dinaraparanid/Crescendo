package com.paranid5.crescendo.core.resources.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors

val AppTextSelectionColors
    @Composable
    get() = TextSelectionColors(
        handleColor = colors.selection.selected,
        backgroundColor = colors.selection.selected.copy(alpha = 0.25F),
    )
