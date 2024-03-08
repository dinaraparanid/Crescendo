package com.paranid5.crescendo.presentation.main.playing.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.playing.PlayingUIHandler
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.effects.PlaybackPositionFetcher
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectAudioStatusAsState
import com.paranid5.crescendo.presentation.main.playing.properties.setPlaybackPosition
import com.paranid5.crescendo.presentation.main.playing.rememberIsLiveStreaming
import com.paranid5.crescendo.presentation.ui.extensions.getLightMutedOrPrimary
import com.paranid5.crescendo.core.resources.ui.theme.TransparentUtility
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun PlaybackSlider(
    audioStatus: AudioStatus,
    durationMillis: Long,
    palette: Palette?,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.(curPosition: Long, videoLength: Long, color: Color) -> Unit
) {
    val paletteColor = palette.getLightMutedOrPrimary()

    val isLiveStreaming by rememberIsLiveStreaming(audioStatus)
    val isSliderEnabled by rememberIsSliderEnabled(audioStatus)

    val currentPositionState = remember { mutableLongStateOf(0) }

    val isDraggingState = remember { mutableStateOf(false) }
    val isDragging by isDraggingState

    PlaybackPositionFetcher(
        audioStatus = audioStatus,
        isDragging = isDragging,
        currentPositionState = currentPositionState
    )

    PlaybackSliderContent(
        audioStatus = audioStatus,
        durationMillis = durationMillis,
        paletteColor = paletteColor,
        isLiveStreaming = isLiveStreaming,
        isSliderEnabled = isSliderEnabled,
        isDraggingState = isDraggingState,
        currentPositionState = currentPositionState,
        modifier = modifier,
        content = content
    )
}

@Composable
fun PlaybackSliderContent(
    audioStatus: AudioStatus,
    durationMillis: Long,
    paletteColor: Color,
    isLiveStreaming: Boolean,
    isSliderEnabled: Boolean,
    isDraggingState: MutableState<Boolean>,
    currentPositionState: MutableState<Long>,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.(curPosition: Long, videoLength: Long, color: Color) -> Unit
) {
    val currentPosition by currentPositionState

    Column(modifier) {
        if (!isLiveStreaming)
            PlaybackSliderImpl(
                durationMillis = durationMillis,
                paletteColor = paletteColor,
                audioStatus = audioStatus,
                isSliderEnabled = isSliderEnabled,
                isDraggingState = isDraggingState,
                currentPositionState = currentPositionState,
            )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = if (isLiveStreaming) 10.dp else 0.dp)
        ) {
            content(currentPosition, durationMillis, paletteColor)
        }
    }
}

@Composable
private fun PlaybackSliderImpl(
    audioStatus: AudioStatus,
    durationMillis: Long,
    paletteColor: Color,
    isSliderEnabled: Boolean,
    isDraggingState: MutableState<Boolean>,
    currentPositionState: MutableState<Long>,
    modifier: Modifier = Modifier,
    viewModel: PlayingViewModel = koinActivityViewModel(),
    playingUIHandler: PlayingUIHandler = koinInject(),
) {
    var isDragging by isDraggingState
    var currentPosition by currentPositionState
    val coroutineScope = rememberCoroutineScope()

    Slider(
        modifier = modifier,
        value = currentPosition.toFloat(),
        valueRange = 0F..durationMillis.toFloat(),
        enabled = isSliderEnabled,
        colors = SliderDefaults.colors(
            thumbColor = paletteColor,
            activeTrackColor = paletteColor,
            inactiveTrackColor = TransparentUtility
        ),
        onValueChange = {
            isDragging = true
            currentPosition = it.toLong()
        },
        onValueChangeFinished = {
            coroutineScope.launch {
                viewModel.setPlaybackPosition(audioStatus, currentPosition)
            }

            playingUIHandler.sendSeekToBroadcast(audioStatus, currentPosition)
            isDragging = false
        }
    )
}

@Composable
private fun rememberIsSliderEnabled(
    audioStatus: AudioStatus,
    viewModel: PlayingViewModel = koinActivityViewModel(),
): State<Boolean> {
    val actualAudioStatus by viewModel.collectAudioStatusAsState()

    return remember(actualAudioStatus, audioStatus) {
        derivedStateOf { actualAudioStatus == audioStatus }
    }
}