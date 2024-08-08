package com.paranid5.crescendo.feature.playing.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import com.paranid5.crescendo.feature.playing.view_model.PlayingState

@Composable
internal fun PlaybackPositionFetcherEffect(
    state: PlayingState,
    isDragging: Boolean,
    currentPositionState: MutableState<Long>,
) = LaunchedEffect(state.playbackPosition) {
    if (isDragging.not())
        currentPositionState.value = state.playbackPosition
}
