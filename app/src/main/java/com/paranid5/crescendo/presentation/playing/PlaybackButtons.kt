package com.paranid5.crescendo.presentation.playing

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.R
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.presentation.ui.AudioStatus
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import com.paranid5.crescendo.presentation.ui.extensions.simpleShadow
import org.koin.compose.koinInject

@Composable
fun PlaybackButtons(
    playingPresenter: PlayingPresenter,
    palette: Palette?,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val audioStatus by storageHandler.audioStatusState.collectAsState()
    val currentMetadata by storageHandler.currentMetadataState.collectAsState()

    val isLiveStreaming by remember {
        derivedStateOf {
            audioStatus == AudioStatus.STREAMING && currentMetadata?.isLiveStream == true
        }
    }

    Row(modifier.fillMaxWidth()) {
        PrevButton(
            isLiveStreaming = isLiveStreaming,
            modifier = Modifier.weight(2F),
            palette = palette
        )

        PlayButton(playingPresenter, modifier = Modifier.weight(1F), palette = palette)

        NextButton(
            isLiveStreaming = isLiveStreaming,
            modifier = Modifier.weight(2F),
            palette = palette
        )
    }
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
private fun PrevButton(
    palette: Palette?,
    isLiveStreaming: Boolean,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val paletteColor = palette.getLightMutedOrPrimary()

    IconButton(
        enabled = !isLiveStreaming,
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
private fun NextButton(
    palette: Palette?,
    isLiveStreaming: Boolean,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject()
) {
    val paletteColor = palette.getLightMutedOrPrimary()

    IconButton(
        enabled = !isLiveStreaming,
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