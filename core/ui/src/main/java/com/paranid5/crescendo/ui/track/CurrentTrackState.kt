package com.paranid5.crescendo.ui.track

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import org.koin.compose.koinInject

@Composable
fun currentTrackState(tracksRepository: TracksRepository = koinInject()): State<Track?> =
    tracksRepository.currentTrackFlow.collectLatestAsState(initial = null)
