package com.paranid5.crescendo.playing.presentation.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.playing.presentation.PlayingViewModel
import com.paranid5.crescendo.playing.presentation.properties.compose.collectPlaybackPositionAsState
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun PlaybackPositionFetcher(
    audioStatus: AudioStatus,
    isDragging: Boolean,
    currentPositionState: MutableState<Long>,
    viewModel: PlayingViewModel = koinViewModel()
) {
    val playbackPosition by viewModel.collectPlaybackPositionAsState(audioStatus)
    var currentPosition by currentPositionState

    LaunchedEffect(playbackPosition) {
        if (!isDragging)
            currentPosition = playbackPosition
    }
}