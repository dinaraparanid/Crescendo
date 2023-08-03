package com.paranid5.mediastreamer.presentation.playing

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
import com.paranid5.mediastreamer.presentation.ui.extensions.getLightMutedOrPrimary
import com.paranid5.mediastreamer.presentation.ui.extensions.simpleShadow
import org.koin.compose.koinInject

@Composable
internal fun PlaybackButtons(
    playingPresenter: PlayingPresenter,
    palette: Palette?,
    modifier: Modifier = Modifier
) = Row(modifier.fillMaxWidth()) {
    SeekTo10SecsBackButton(modifier = Modifier.weight(2F), palette = palette)
    PlayButton(playingPresenter, modifier = Modifier.weight(1F), palette = palette)
    SeekTo10SecsForwardButton(modifier = Modifier.weight(2F), palette = palette)
}

@Composable
private fun PlayButton(
    playingPresenter: PlayingPresenter,
    palette: Palette?,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject(),
) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val isPlaying by playingPresenter.isPlayingState.collectAsState()

    when {
        isPlaying -> IconButton(
            modifier = modifier.simpleShadow(color = paletteColor),
            onClick = { playingUIHandler.sendPauseBroadcast() }
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                painter = painterResource(R.drawable.pause),
                contentDescription = stringResource(R.string.pause),
                tint = paletteColor
            )
        }

        else -> IconButton(
            modifier = modifier.simpleShadow(color = paletteColor),
            onClick = { playingUIHandler.startStreamingOrSendResumeBroadcast() }
        ) {
            Icon(
                modifier = Modifier.size(50.dp),
                painter = painterResource(R.drawable.play),
                contentDescription = stringResource(R.string.play),
                tint = paletteColor
            )
        }
    }
}

@Composable
private fun SeekTo10SecsBackButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val paletteColor = palette.getLightMutedOrPrimary()

    IconButton(
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = { playingUIHandler.sendOnPrevButtonClickedBroadcast() }
    ) {
        Icon(
            modifier = Modifier.width(100.dp).height(50.dp),
            painter = painterResource(R.drawable.prev_track),
            contentDescription = stringResource(R.string.ten_secs_back),
            tint = paletteColor
        )
    }
}

@Composable
private fun SeekTo10SecsForwardButton(
    palette: Palette?,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val paletteColor = palette.getLightMutedOrPrimary()

    IconButton(
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = { playingUIHandler.sendOnNextButtonClickedBroadcast() }
    ) {
        Icon(
            modifier = Modifier.width(100.dp).height(50.dp),
            painter = painterResource(R.drawable.next_track),
            contentDescription = stringResource(R.string.ten_secs_forward),
            tint = paletteColor
        )
    }
}