package com.paranid5.crescendo.feature.playing.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.colors
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.feature.playing.presentation.effect.PlaybackPositionFetcherEffect
import com.paranid5.crescendo.feature.playing.presentation.ui.composition_local.LocalPalette
import com.paranid5.crescendo.feature.playing.view_model.PlayingState
import com.paranid5.crescendo.utils.extensions.getBrightDominantOrPrimary

@Composable
internal fun PlaybackSlider(
    state: PlayingState,
    seekTo: (position: Long) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.(curPosition: Long, videoLength: Long, color: Color) -> Unit
) {
    val currentPositionState = remember { mutableLongStateOf(0) }
    val isDraggingState = remember { mutableStateOf(false) }
    val isDragging by isDraggingState

    PlaybackPositionFetcherEffect(
        state = state,
        isDragging = isDragging,
        currentPositionState = currentPositionState,
    )

    PlaybackSliderContent(
        state = state,
        seekTo = seekTo,
        isDraggingState = isDraggingState,
        currentPositionState = currentPositionState,
        modifier = modifier,
        content = content,
    )
}

@Composable
internal fun PlaybackSliderContent(
    state: PlayingState,
    seekTo: (position: Long) -> Unit,
    isDraggingState: MutableState<Boolean>,
    currentPositionState: MutableState<Long>,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.(curPosition: Long, videoLength: Long, color: Color) -> Unit
) {
    val color = LocalPalette.current.getBrightDominantOrPrimary()
    val currentPosition by currentPositionState

    Column(modifier) {
        if (state.isLiveStreaming.not())
            PlaybackSliderImpl(
                state = state,
                seekTo = seekTo,
                isDraggingState = isDraggingState,
                currentPositionState = currentPositionState,
            )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    top = dimensions.padding.run {
                        if (state.isLiveStreaming) medium else zero
                    }
                )
        ) {
            content(currentPosition, state.durationMillis, color)
        }
    }
}

@Composable
private fun PlaybackSliderImpl(
    state: PlayingState,
    seekTo: (position: Long) -> Unit,
    isDraggingState: MutableState<Boolean>,
    currentPositionState: MutableState<Long>,
    modifier: Modifier = Modifier,
) {
    val color = LocalPalette.current.getBrightDominantOrPrimary()

    var isDragging by isDraggingState
    var currentPosition by currentPositionState

    Slider(
        modifier = modifier,
        value = currentPosition.toFloat(),
        valueRange = 0F..state.durationMillis.toFloat(),
        enabled = state.isScreenAudioStatusActual,
        colors = SliderDefaults.colors(
            thumbColor = color,
            activeTrackColor = color,
            inactiveTrackColor = colors.utils.transparentUtility,
        ),
        onValueChange = {
            isDragging = true
            currentPosition = it.toLong()
        },
        onValueChangeFinished = {
            seekTo(currentPosition)
            isDragging = false
        }
    )
}
