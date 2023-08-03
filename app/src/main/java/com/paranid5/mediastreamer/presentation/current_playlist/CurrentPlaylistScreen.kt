package com.paranid5.mediastreamer.presentation.current_playlist

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.paranid5.mediastreamer.domain.StorageHandler
import com.paranid5.mediastreamer.domain.services.track_service.TrackServiceAccessor
import com.paranid5.mediastreamer.presentation.tracks.DismissableTrackList
import com.paranid5.mediastreamer.presentation.tracks.TrackList
import org.koin.compose.koinInject

@Composable
fun CurrentPlaylistScreen(
    modifier: Modifier = Modifier,
    storageHandler: StorageHandler = koinInject(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val currentPlaylistMb by storageHandler.currentPlaylistState.collectAsState()
    val currentTrackIndex by storageHandler.currentTrackIndexState.collectAsState()
    val currentPlaylist by remember { derivedStateOf { currentPlaylistMb ?: listOf() } }
    val scrollingState = rememberLazyListState()

    DismissableTrackList(
        tracks = currentPlaylist,
        scrollingState = scrollingState,
        modifier = modifier,
        storageHandler = storageHandler
    ) { index, _ ->
        if (index != currentTrackIndex) {
            val newPlaylist = currentPlaylist.take(index) + currentPlaylist.drop(index + 1)
            storageHandler.storeCurrentPlaylist(newPlaylist)

            if (index < currentTrackIndex)
                storageHandler.storeCurrentTrackIndex(currentTrackIndex - 1)

            trackServiceAccessor.removeFromPlaylist(index)
        }
    }
}