package com.paranid5.crescendo.ui.track.item

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

@Composable
internal fun TrackTextLabel(
    text: String,
    textColor: Color,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) = Text(
    modifier = modifier,
    text = text,
    color = textColor,
    fontSize = fontSize,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis
)