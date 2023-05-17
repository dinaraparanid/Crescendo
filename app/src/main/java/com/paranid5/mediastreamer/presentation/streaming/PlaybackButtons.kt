package com.paranid5.mediastreamer.presentation.streaming

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.ui.extensions.getLightVibrantOrPrimary
import com.paranid5.mediastreamer.presentation.ui.extensions.simpleShadow
import org.koin.compose.koinInject

@Composable
fun PlaybackButtons(
    streamingPresenter: StreamingPresenter,
    palette: Palette?,
    modifier: Modifier = Modifier
) = Row(modifier.fillMaxWidth()) {
    SeekTo10SecsBackButton(modifier = Modifier.weight(2F), palette = palette)
    PlayButton(streamingPresenter, modifier = Modifier.weight(1F), palette = palette)
    SeekTo10SecsForwardButton(modifier = Modifier.weight(2F), palette = palette)
}

@Composable
private fun PlayButton(
    streamingPresenter: StreamingPresenter,
    palette: Palette?,
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = koinInject(),
) {
    val lightVibrantColor = palette.getLightVibrantOrPrimary()
    val isPlaying by streamingPresenter.isPlayingState.collectAsState()

    when {
        isPlaying -> IconButton(
            modifier = modifier.simpleShadow(color = lightVibrantColor),
            onClick = { streamingUIHandler.sendPauseBroadcast() }
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                painter = painterResource(R.drawable.pause),
                contentDescription = stringResource(R.string.pause),
                tint = lightVibrantColor
            )
        }

        else -> IconButton(
            modifier = modifier.simpleShadow(color = lightVibrantColor),
            onClick = { streamingUIHandler.startStreamingOrSendResumeBroadcast() }
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                painter = painterResource(R.drawable.play),
                contentDescription = stringResource(R.string.play),
                tint = lightVibrantColor
            )
        }
    }
}

@Composable
private fun SeekTo10SecsBackButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = koinInject()
) {
    val lightVibrantColor = palette.getLightVibrantOrPrimary()

    IconButton(
        modifier = modifier.simpleShadow(color = lightVibrantColor),
        onClick = { streamingUIHandler.sendSeekTo10SecsBackBroadcast() }
    ) {
        Icon(
            modifier = Modifier.width(100.dp).height(50.dp),
            painter = painterResource(R.drawable.prev_track),
            contentDescription = stringResource(R.string.ten_secs_back),
            tint = lightVibrantColor
        )
    }
}

@Composable
private fun SeekTo10SecsForwardButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = koinInject()
) {
    val lightVibrantColor = palette.getLightVibrantOrPrimary()

    IconButton(
        modifier = modifier.simpleShadow(color = lightVibrantColor),
        onClick = { streamingUIHandler.sendSeekTo10SecsForwardBroadcast() }
    ) {
        Icon(
            modifier = Modifier.width(100.dp).height(50.dp),
            painter = painterResource(R.drawable.next_track),
            contentDescription = stringResource(R.string.ten_secs_forward),
            tint = lightVibrantColor
        )
    }
}