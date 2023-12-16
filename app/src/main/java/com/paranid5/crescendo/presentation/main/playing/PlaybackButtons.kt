package com.paranid5.crescendo.presentation.main.playing

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.IS_PLAYING
import com.paranid5.crescendo.R
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import com.paranid5.crescendo.presentation.ui.extensions.simpleShadow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

@Composable
fun PlaybackButtons(
    palette: Palette?,
    audioStatus: AudioStatus,
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject()
) {
    val currentMetadata by storageHandler.currentMetadataState.collectAsState()

    val isLiveStreaming by remember {
        derivedStateOf {
            audioStatus == AudioStatus.STREAMING && currentMetadata?.isLiveStream == true
        }
    }

    Row(modifier.fillMaxWidth()) {
        PrevButton(
            enabled = !isLiveStreaming,
            modifier = Modifier.weight(2F),
            palette = palette,
            audioStatus = audioStatus
        )

        PlayButton(
            modifier = Modifier.weight(1F),
            palette = palette,
            audioStatus = audioStatus
        )

        NextButton(
            enabled = !isLiveStreaming,
            modifier = Modifier.weight(2F),
            palette = palette,
            audioStatus = audioStatus
        )
    }
}

@Composable
private fun PlayButton(
    palette: Palette?,
    audioStatus: AudioStatus,
    modifier: Modifier = Modifier,
    isPlayingState: MutableStateFlow<Boolean> = koinInject(named(IS_PLAYING)),
    playingUIHandler: PlayingUIHandler = koinInject(),
    storageHandler: StorageHandler = koinInject()
) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val isPlayerPlaying by isPlayingState.collectAsState()
    val actualAudioStatus by storageHandler.audioStatusState.collectAsState()

    val isPlaying by remember {
        derivedStateOf { isPlayerPlaying && actualAudioStatus == audioStatus }
    }

    val coroutineScope = rememberCoroutineScope()

    when {
        isPlaying -> IconButton(
            modifier = modifier.simpleShadow(color = paletteColor),
            onClick = {
                coroutineScope.launch {
                    storageHandler.storeAudioStatus(audioStatus)
                }

                playingUIHandler.sendPauseBroadcast(audioStatus)
            }
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
            onClick = {
                coroutineScope.launch {
                    storageHandler.storeAudioStatus(audioStatus)
                }

                playingUIHandler.startStreamingOrSendResumeBroadcast(audioStatus)
            }
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
    audioStatus: AudioStatus,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject(),
    storageHandler: StorageHandler = koinInject()
) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val coroutineScope = rememberCoroutineScope()

    IconButton(
        enabled = enabled,
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = {
            coroutineScope.launch {
                storageHandler.storeAudioStatus(audioStatus)
            }

            playingUIHandler.sendOnPrevButtonClickedBroadcast(audioStatus)
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.prev_track),
            contentDescription = stringResource(R.string.ten_secs_back),
            tint = paletteColor,
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
        )
    }
}

@Composable
private fun NextButton(
    palette: Palette?,
    audioStatus: AudioStatus,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject(),
    storageHandler: StorageHandler = koinInject()
) {
    val paletteColor = palette.getLightMutedOrPrimary()
    val coroutineScope = rememberCoroutineScope()

    IconButton(
        enabled = enabled,
        modifier = modifier.simpleShadow(color = paletteColor),
        onClick = {
            coroutineScope.launch {
                storageHandler.storeAudioStatus(audioStatus)
            }

            playingUIHandler.sendOnNextButtonClickedBroadcast(audioStatus)
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.next_track),
            contentDescription = stringResource(R.string.ten_secs_forward),
            tint = paletteColor,
            modifier = Modifier
                .width(100.dp)
                .height(50.dp)
        )
    }
}