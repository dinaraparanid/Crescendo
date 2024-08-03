package com.paranid5.crescendo.ui.track.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.typography

@Composable
fun TrackTitle(
    trackTitle: String,
    textColor: Color,
    modifier: Modifier = Modifier
) = TrackTextLabel(
    modifier = modifier,
    text = trackTitle,
    textColor = textColor,
    style = typography.h.h3,
)