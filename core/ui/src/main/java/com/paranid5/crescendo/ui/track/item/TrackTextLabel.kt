package com.paranid5.crescendo.ui.track.item

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

@Composable
internal fun TrackTextLabel(
    text: String,
    textColor: Color,
    style: TextStyle,
    modifier: Modifier = Modifier
) = Text(
    modifier = modifier,
    text = text,
    color = textColor,
    style = style,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)