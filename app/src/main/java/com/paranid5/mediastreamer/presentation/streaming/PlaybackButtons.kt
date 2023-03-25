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
import com.paranid5.mediastreamer.R
import com.paranid5.mediastreamer.presentation.ui.extensions.primaryColorShadow
import com.paranid5.mediastreamer.presentation.ui.theme.LocalAppColors
import com.paranid5.mediastreamer.utils.BroadcastReceiver
import kotlinx.coroutines.flow.update
import org.koin.androidx.compose.get

@Composable
fun PlaybackButtons(streamingPresenter: StreamingPresenter, modifier: Modifier = Modifier) =
    Row(modifier.fillMaxWidth()) {
        SeekTo10SecsBackButton(Modifier.weight(2F))
        PlayButton(streamingPresenter, modifier = Modifier.weight(1F))
        SeekTo10SecsForwardButton(Modifier.weight(2F))
    }

@Composable
private fun PlayButton(
    streamingPresenter: StreamingPresenter,
    modifier: Modifier = Modifier,
    streamingUIHandler: StreamingUIHandler = get()
) {
    val colors = LocalAppColors.current.value
    val isPlaying by streamingPresenter.isPlaying.collectAsState()

    BroadcastReceiver(action = Broadcast_IS_PLAYING_CHANGED) { _, intent ->
        streamingPresenter.isPlaying.update { intent!!.getBooleanExtra(IS_PLAYING_ARG, !isPlaying) }
    }

    when {
        isPlaying -> IconButton(
            modifier = modifier.primaryColorShadow(),
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
            modifier = modifier.primaryColorShadow(),
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
        modifier = modifier.primaryColorShadow(),
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
        modifier = modifier.primaryColorShadow(),
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