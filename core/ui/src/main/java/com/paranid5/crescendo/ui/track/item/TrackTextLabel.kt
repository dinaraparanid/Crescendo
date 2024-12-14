package com.paranid5.crescendo.ui.track.item

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.paranid5.crescendo.ui.foundation.AppText

@Composable
internal fun TrackTextLabel(
    text: String,
    textColor: Color,
    style: TextStyle,
    modifier: Modifier = Modifier
) = AppText(
    modifier = modifier,
    text = text,
    style = style.copy(color = textColor),
    maxLines = 1,
)