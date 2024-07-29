package com.paranid5.crescendo.playing.presentation.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.domain.stream.currentMetadataDurationMillisFlow
import com.paranid5.crescendo.playing.presentation.PlayingViewModel
import com.paranid5.crescendo.playing.presentation.properties.durationMillisFlow
import com.paranid5.crescendo.playing.presentation.properties.playbackPositionFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
internal fun PlayingViewModel.collectCurrentMetadataAsState(initial: VideoMetadata? = null) =
    currentMetadataFlow.collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectCurrentTrackAsState(initial: DefaultTrack? = null) =
    currentTrackFlow.collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectPlayingUrlAsState(initial: String = "") =
    playingUrlFlow.collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectPlaybackPositionAsState(audioStatus: AudioStatus, initial: Long = 0) =
    playbackPositionFlow(audioStatus).collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectDurationMillisAsState(audioStatus: AudioStatus, initial: Long = 0) =
    durationMillisFlow(audioStatus).collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectIsRepeatingAsState(initial: Boolean = false) =
    isRepeatingFlow.collectLatestAsState(initial)

@Composable
internal fun PlayingViewModel.collectCurrentMetadataDurationMillisAsState(initial: Long = 0) =
    currentMetadataDurationMillisFlow.collectLatestAsState(initial)