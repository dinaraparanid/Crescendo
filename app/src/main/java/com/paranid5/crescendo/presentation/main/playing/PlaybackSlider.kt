package com.paranid5.crescendo.presentation.main.playing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.domain.StorageHandler
import com.paranid5.crescendo.presentation.main.AudioStatus
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import com.paranid5.crescendo.presentation.ui.theme.TransparentUtility
import com.paranid5.crescendo.presentation.ui.utils.BroadcastReceiver
import org.koin.compose.koinInject

@Composable
fun PlaybackSlider(
    length: Long,
    palette: Palette?,
    audioStatus: AudioStatus?,
    modifier: Modifier = Modifier,
    playingUIHandler: PlayingUIHandler = koinInject(),
    storageHandler: StorageHandler = koinInject(),
    content: @Composable RowScope.(curPosition: Long, videoLength: Long, color: Color) -> Unit
) {
    val paletteColor = palette.getLightMutedOrPrimary()
    var curPosition by remember { mutableLongStateOf(0L) }

    val currentMetadata by storageHandler.currentMetadataState.collectAsState()
    val actualAudioStatus by storageHandler.audioStatusState.collectAsState()

    val isLiveStreaming by remember {
        derivedStateOf {
            audioStatus == AudioStatus.STREAMING && currentMetadata?.isLiveStream == true
        }
    }

    val isSliderEnabled by remember {
        derivedStateOf { actualAudioStatus == audioStatus }
    }

    fun getDefaultPlaybackPosition() = when (audioStatus) {
        AudioStatus.STREAMING -> storageHandler.streamPlaybackPositionState.value
        else -> storageHandler.tracksPlaybackPositionState.value
    }

    LaunchedEffect(key1 = true) {
        curPosition = getDefaultPlaybackPosition()
    }

    BroadcastReceiver(action = Broadcast_CUR_POSITION_CHANGED) { _, intent ->
        curPosition = when {
            isSliderEnabled -> intent!!.getLongExtra(CUR_POSITION_ARG, 0)
            else -> getDefaultPlaybackPosition()
        }
    }

    Column(modifier.padding(horizontal = 10.dp)) {
        if (!isLiveStreaming)
            Slider(
                value = curPosition.toFloat(),
                valueRange = 0F..length.toFloat(),
                enabled = isSliderEnabled,
                colors = SliderDefaults.colors(
                    thumbColor = paletteColor,
                    activeTrackColor = paletteColor,
                    inactiveTrackColor = TransparentUtility
                ),
                onValueChange = { curPosition = it.toLong() },
                onValueChangeFinished = {
                    playingUIHandler.sendSeekToBroadcast(audioStatus, curPosition)
                }
            )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = if (isLiveStreaming) 10.dp else 0.dp)
        ) {
            content(curPosition, length, paletteColor)
        }
    }
}