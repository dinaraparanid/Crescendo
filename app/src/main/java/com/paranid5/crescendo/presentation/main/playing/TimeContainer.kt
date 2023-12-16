package com.paranid5.crescendo.presentation.main.playing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
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
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.utils.extensions.timeString
import com.paranid5.crescendo.presentation.ui.theme.LocalAppColors
import com.paranid5.crescendo.services.stream_service.StreamServiceAccessor
import org.koin.compose.koinInject

context(RowScope)
@Composable
fun TimeContainer(isLiveStreaming: Boolean, curPosition: Long, videoLength: Long, color: Color) =
    when {
        isLiveStreaming -> Box(Modifier.fillMaxWidth()) {
            LiveSeeker(color = color, modifier = Modifier.align(Alignment.Center))
        }

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
fun LiveSeeker(
    color: Color,
    modifier: Modifier = Modifier,
    streamServiceAccessor: StreamServiceAccessor = koinInject()
) {
    val colors = LocalAppColors.current.value

    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(20.dp),
        onClick = { streamServiceAccessor.sendSeekToBroadcast(0) }
    ) {
        Text(
            text = stringResource(R.string.live),
            color = colors.primary,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center
        )
    }
}