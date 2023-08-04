package com.paranid5.crescendo.presentation.playing

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.data.utils.extensions.timeString

@Composable
fun TimeText(time: Long, color: Color, modifier: Modifier = Modifier) =
    Text(text = time.timeString, color = color, modifier = modifier)