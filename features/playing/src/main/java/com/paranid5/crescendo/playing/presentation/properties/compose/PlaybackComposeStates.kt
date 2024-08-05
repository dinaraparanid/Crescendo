package com.paranid5.crescendo.playing.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.domain.stream.currentMetadataDurationMillisFlow
import com.paranid5.crescendo.playing.presentation.properties.durationMillisFlow
import com.paranid5.crescendo.playing.presentation.properties.playbackPositionFlow
import com.paranid5.crescendo.playing.view_model.PlayingViewModel
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import kotlinx.coroutines.flow.map

@Composable
internal fun PlayingViewModel.collectCurrentMetadataAsState(initial: VideoMetadata? = null) =
    currentMetadataFlow.collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectCurrentTrackAsState(initial: TrackUiState? = null) =
    currentTrackFlow
        .map { it?.let(TrackUiState.Companion::fromDTO) }
        .collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectPlayingUrlAsState(initial: String = "") =
    playingUrlFlow.collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectPlaybackPositionAsState(
    audioStatus: AudioStatus,
    initial: Long = 0,
) = playbackPositionFlow(audioStatus).collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectDurationMillisAsState(
    audioStatus: AudioStatus,
    initial: Long = 0,
) = durationMillisFlow(audioStatus).collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectIsRepeatingAsState(initial: Boolean = false) =
    isRepeatingFlow.collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectCurrentMetadataDurationMillisAsState(initial: Long = 0) =
    currentMetadataDurationMillisFlow.collectLatestAsState(initial)