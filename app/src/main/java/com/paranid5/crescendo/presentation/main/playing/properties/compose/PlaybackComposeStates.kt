package com.paranid5.crescendo.presentation.main.playing.properties.compose

import androidx.compose.runtime.Composable
import com.paranid5.crescendo.core.common.AudioStatus
import com.paranid5.crescendo.core.common.metadata.VideoMetadata
import com.paranid5.crescendo.core.common.tracks.DefaultTrack
import com.paranid5.crescendo.domain.sources.stream.currentMetadataDurationMillisFlow
import com.paranid5.crescendo.presentation.main.playing.PlayingViewModel
import com.paranid5.crescendo.presentation.main.playing.properties.durationMillisFlow
import com.paranid5.crescendo.presentation.main.playing.properties.playbackPositionFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState

@Composable
fun PlayingViewModel.collectCurrentMetadataAsState(initial: VideoMetadata? = null) =
    currentMetadataFlow.collectLatestAsState(initial)

@Composable
fun PlayingViewModel.collectCurrentTrackAsState(initial: DefaultTrack? = null) =
    currentTrackFlow.collectLatestAsState(initial)

@Composable
fun PlayingViewModel.collectCurrentUrlAsState(initial: String = "") =
    currentUrlFlow.collectLatestAsState(initial)

@Composable
fun PlayingViewModel.collectPlaybackPositionAsState(audioStatus: AudioStatus, initial: Long = 0) =
    playbackPositionFlow(audioStatus).collectLatestAsState(initial)

@Composable
fun PlayingViewModel.collectDurationMillisAsState(audioStatus: AudioStatus, initial: Long = 0) =
    durationMillisFlow(audioStatus).collectLatestAsState(initial)

@Composable
fun PlayingViewModel.collectIsRepeatingAsState(initial: Boolean = false) =
    isRepeatingFlow.collectLatestAsState(initial)

@Composable
fun PlayingViewModel.collectCurrentMetadataDurationMillisAsState(initial: Long = 0) =
    currentMetadataDurationMillisFlow.collectLatestAsState(initial)