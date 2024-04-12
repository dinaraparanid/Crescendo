package com.paranid5.crescendo.current_playlist.presentation.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.current_playlist.presentation.CurrentPlaylistViewModel
import com.paranid5.crescendo.current_playlist.domain.tryDismissTrack
import com.paranid5.crescendo.current_playlist.domain.updateCurrentPlaylistAfterDrag
import com.paranid5.crescendo.current_playlist.presentation.properties.compose.collectCurrentPlaylistAsState
import com.paranid5.crescendo.current_playlist.presentation.properties.compose.collectCurrentTrackIndexAsState
import com.paranid5.crescendo.system.services.track.TrackServiceAccessor
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
internal fun CurrentPlaylistTrackList(
    modifier: Modifier = Modifier,
    viewModel: CurrentPlaylistViewModel = koinViewModel(),
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val currentPlaylist by viewModel.collectCurrentPlaylistAsState()
    val currentTrackIndex by viewModel.collectCurrentTrackIndexAsState()

    DraggableTrackList(
        tracks = currentPlaylist,
        currentTrackIndex = currentTrackIndex,
        modifier = modifier,
        onTrackDismissed = { index, track ->
            tryDismissTrack(
                source = viewModel,
                index = index,
                track = track,
                currentPlaylist = currentPlaylist,
                currentTrackIndex = currentTrackIndex,
            )
        },
        onTrackDragged = { newTracks, newCurTrackIndex ->
            updateCurrentPlaylistAfterDrag(
                publisher = viewModel,
                newTracks = newTracks,
                newCurTrackIndex = newCurTrackIndex,
                trackServiceAccessor = trackServiceAccessor
            )
        }
    )
}