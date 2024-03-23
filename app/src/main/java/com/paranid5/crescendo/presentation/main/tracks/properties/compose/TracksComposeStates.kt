package com.paranid5.crescendo.presentation.main.tracks.properties.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.data.properties.currentTrackIndexFlow
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import com.paranid5.crescendo.presentation.main.tracks.properties.shownTracksFlow
import com.paranid5.crescendo.presentation.main.tracks.properties.shownTracksNumberFlow
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.koinInject

@Composable
fun TracksViewModel.collectTracksAsState() =
    tracksState.collectLatestAsState()

@Composable
fun TracksViewModel.collectTrackOrderAsState() =
    trackOrderFlow.collectLatestAsState(initial = com.paranid5.crescendo.core.common.tracks.TrackOrder.default)

@Composable
fun TracksViewModel.collectShownTracksAsState() =
    shownTracksFlow.collectLatestAsState(initial = persistentListOf())

@Composable
fun TracksViewModel.collectShownTracksNumberAsState() =
    shownTracksNumberFlow.collectLatestAsState(initial = 0)

@Composable
internal fun currentTrackState(
    currentPlaylistRepository: CurrentPlaylistRepository = koinInject(),
    storageRepository: StorageRepository = koinInject(),
): State<com.paranid5.crescendo.core.common.tracks.Track?> {
    val curPlaylist by currentPlaylistRepository
        .tracksFlow
        .collectLatestAsState(initial = persistentListOf())

    val currentIndex by storageRepository
        .currentTrackIndexFlow
        .collectLatestAsState(initial = 0)

    return remember(currentIndex, curPlaylist) {
        derivedStateOf { curPlaylist.getOrNull(currentIndex) }
    }
}