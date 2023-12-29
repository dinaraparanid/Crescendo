package com.paranid5.crescendo.presentation.main.playing.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.paranid5.crescendo.domain.media.AudioStatus
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.compose.collectPlaybackPositionAsState

@Composable
fun PlaybackPositionFetcher(
    viewModel: PlayingViewModel,
    audioStatus: AudioStatus,
    isDragging: Boolean,
    currentPositionState: MutableState<Long>
) {
    val playbackPosition by viewModel.collectPlaybackPositionAsState(audioStatus)
    var currentPosition by currentPositionState

    LaunchedEffect(playbackPosition) {
        if (!isDragging)
            currentPosition = playbackPosition
    }
}