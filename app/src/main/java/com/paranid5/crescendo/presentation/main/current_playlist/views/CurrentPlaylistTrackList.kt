package com.paranid5.crescendo.presentation.main.current_playlist.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.koinActivityViewModel
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import com.paranid5.crescendo.presentation.main.current_playlist.properties.compose.collectCurrentPlaylistAsState
import com.paranid5.crescendo.presentation.main.current_playlist.properties.compose.collectCurrentTrackIndexAsState
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.koinInject

@Composable
fun CurrentPlaylistTrackList(
    modifier: Modifier = Modifier,
    viewModel: CurrentPlaylistViewModel = koinActivityViewModel(),
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
                viewModel = viewModel,
                index = index,
                track = track,
                currentPlaylist = currentPlaylist,
                currentTrackIndex = currentTrackIndex,
            )
        },
        onTrackDragged = { newTracks, newCurTrackIndex ->
            updateCurrentPlaylist(
                newTracks = newTracks,
                newCurTrackIndex = newCurTrackIndex,
                viewModel = viewModel,
                trackServiceAccessor = trackServiceAccessor
            )
        }
    )
}

private fun tryDismissTrack(
    viewModel: CurrentPlaylistViewModel,
    index: Int,
    track: Track,
    currentPlaylist: ImmutableList<Track>,
    currentTrackIndex: Int,
): Boolean {
    if (index == currentTrackIndex)
        return false

    viewModel.setPlaylistDismissMediator(
        (currentPlaylist.take(index) + currentPlaylist.drop(index + 1))
            .toImmutableList()
    )

    viewModel.setTrackIndexDismissMediator(index)
    viewModel.setTrackPathDismissKey(track.path)
    return true
}

private suspend fun updateCurrentPlaylist(
    newTracks: ImmutableList<Track>,
    newCurTrackIndex: Int,
    viewModel: CurrentPlaylistViewModel,
    trackServiceAccessor: TrackServiceAccessor
) {
    viewModel.setCurrentPlaylist(newTracks)
    viewModel.setCurrentTrackIndex(newCurTrackIndex)
    trackServiceAccessor.updatePlaylistAfterDrag(newTracks, newCurTrackIndex)
}