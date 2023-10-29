package com.paranid5.crescendo.presentation.playing

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.utils.extensions.timeString

context(RowScope)
@Composable
fun TimeContainer(isLiveStreaming: Boolean, curPosition: Long, videoLength: Long, color: Color) =
    when {
        isLiveStreaming -> LiveText(color = color, modifier = Modifier.fillMaxWidth())

        else -> {
            TimeText(curPosition, color)
            Spacer(Modifier.weight(1F))
            TimeText(videoLength, color)
        }
    }

@Composable
fun TimeText(time: Long, color: Color, modifier: Modifier = Modifier) =
    Text(text = time.timeString, color = color, modifier = modifier)

@Composable
fun LiveText(color: Color, modifier: Modifier = Modifier) =
    Text(
        text = stringResource(R.string.live),
        color = color,
        modifier = modifier,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        textAlign = TextAlign.Center
    )