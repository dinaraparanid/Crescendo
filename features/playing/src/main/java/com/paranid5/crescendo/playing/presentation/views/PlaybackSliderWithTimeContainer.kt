package com.paranid5.crescendo.playing.presentation.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.resources.R
import com.paranid5.crescendo.utils.extensions.timeString
import com.paranid5.crescendo.core.resources.ui.theme.LocalAppColors
import com.paranid5.crescendo.system.services.stream.StreamServiceAccessor
import com.paranid5.crescendo.system.services.stream.sendSeekToBroadcast
import org.koin.compose.koinInject

@Composable
internal fun PlaybackSliderWithTimeContainer(
    durationMillis: Long,
    palette: Palette?,
    audioStatus: AudioStatus,
    isLiveStreaming: Boolean,
    modifier: Modifier = Modifier
) = PlaybackSlider(
    durationMillis = durationMillis,
    palette = palette,
    audioStatus = audioStatus,
    modifier = modifier
) { curPosition, videoLength, color ->
    when {
        isLiveStreaming -> Box(Modifier.fillMaxWidth()) {
            LiveSeeker(
                color = color,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        else -> {
            TimeText(curPosition, color)
            Spacer(Modifier.weight(1F))
            TimeText(videoLength, color)
        }
    }
}

@Composable
internal fun TimeText(time: Long, color: Color, modifier: Modifier = Modifier) =
    Text(
        text = time.timeString,
        color = color,
        modifier = modifier,
        fontSize = 16.sp
    )

@Composable
internal fun LiveSeeker(
    color: Color,
    modifier: Modifier = Modifier,
    streamServiceAccessor: StreamServiceAccessor = koinInject()
) = Button(
    modifier = modifier,
    colors = ButtonDefaults.buttonColors(containerColor = color),
    shape = RoundedCornerShape(20.dp),
    content = { LiveLabel() },
    onClick = { streamServiceAccessor.sendSeekToBroadcast(0) }
)

@Composable
private fun LiveLabel(modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current

    Text(
        text = stringResource(R.string.live),
        color = colors.primary,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}