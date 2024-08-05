package com.paranid5.crescendo.ui.track.item

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme

@Composable
fun rememberTrackBackgroundColors(isCurrent: Boolean): State<Color> {
    val appColors = AppTheme.colors
    return remember(isCurrent, appColors) {
        derivedStateOf {
            when {
                isCurrent -> appColors.selection.selected.copy(alpha = 0.25F)
                else -> Color.Transparent
            }
        }
    }
}

@Composable
fun rememberTrackContentColor(isCurrent: Boolean): State<Color> {
    val appColors = AppTheme.colors
    return remember(isCurrent, appColors) {
        derivedStateOf {
            when {
                isCurrent -> appColors.selection.selected
                else -> appColors.text.primary
            }
        }
    }
}
