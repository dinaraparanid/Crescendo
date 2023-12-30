package com.paranid5.crescendo.presentation.main.tracks.views.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun TrackTitle(
    trackTitle: String,
    textColor: Color,
    modifier: Modifier = Modifier
) = TrackTextLabel(
    modifier = modifier,
    text = trackTitle,
    textColor = textColor,
    fontSize = 18.sp,
)