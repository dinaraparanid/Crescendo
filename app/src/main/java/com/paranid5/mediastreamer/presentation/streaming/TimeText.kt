package com.paranid5.mediastreamer.presentation.streaming

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.paranid5.mediastreamer.data.utils.extensions.timeString

@Composable
internal fun TimeText(time: Long, color: Color, modifier: Modifier = Modifier) =
    Text(text = time.timeString, color = color, modifier = modifier)