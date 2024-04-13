package com.paranid5.crescendo.ui.track

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.currentTrackIndexFlow
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.utils.extensions.collectLatestAsState
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.koinInject

@Composable
fun currentTrackState(
    currentPlaylistRepository: CurrentPlaylistRepository = koinInject(),
    storageRepository: StorageRepository = koinInject(),
): State<Track?> {
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