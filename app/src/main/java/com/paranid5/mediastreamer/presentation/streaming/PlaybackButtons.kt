package com.paranid5.mediastreamer.presentation.streaming

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.BroadcastReceiver
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import org.koin.androidx.compose.get

@Composable
fun PlaybackButtons(modifier: Modifier = Modifier) =
    Row(modifier.fillMaxWidth()) {
        SeekTo10SecsBackButton(Modifier.weight(2F))
        PlayButton(Modifier.weight(1F))
        SeekTo10SecsForwardButton(Modifier.weight(2F))
    }

@Composable
private fun PlayButton(
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value
    var isPlaying by remember { mutableStateOf(false) }

    BroadcastReceiver(action = Broadcast_IS_PLAYING_CHANGED) { _, intent ->
        isPlaying = intent!!.getBooleanExtra(IS_PLAYING_ARG, !isPlaying)
    }

    when {
        isPlaying -> IconButton(
            modifier = modifier,
            onClick = { streamingUIHandler.sendPauseBroadcast() }
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                painter = painterResource(R.drawable.pause),
                contentDescription = stringResource(R.string.pause),
                tint = colors.primary
            )
        }

        else -> IconButton(
            modifier = modifier,
            onClick = { streamingUIHandler.startStreamingOrSendResumeBroadcast() }
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                painter = painterResource(R.drawable.play),
                contentDescription = stringResource(R.string.play),
                tint = colors.primary
            )
        }
    }
}

@Composable
private fun SeekTo10SecsBackButton(
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value

    IconButton(
        modifier = modifier,
        onClick = { streamingUIHandler.sendSeekTo10SecsBackBroadcast() }
    ) {
        Icon(
            modifier = Modifier.width(100.dp).height(50.dp),
            painter = painterResource(R.drawable.prev_track),
            contentDescription = stringResource(R.string.ten_secs_back),
            tint = colors.primary
        )
    }
}

@Composable
private fun SeekTo10SecsForwardButton(
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value

    IconButton(
        modifier = modifier,
        onClick = { streamingUIHandler.sendSeekTo10SecsForwardBroadcast() }
    ) {
        Icon(
            modifier = Modifier.width(100.dp).height(50.dp),
            painter = painterResource(R.drawable.next_track),
            contentDescription = stringResource(R.string.ten_secs_forward),
            tint = colors.primary
        )
    }
}