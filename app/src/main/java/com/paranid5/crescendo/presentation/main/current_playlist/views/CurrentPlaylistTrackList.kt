package com.paranid5.crescendo.presentation.main.current_playlist.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.domain.tracks.DefaultTrack
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
    playlistDismissMediatorState: MutableState<ImmutableList<DefaultTrack>>,
    trackIndexDismissMediatorState: MutableState<Int>,
    trackPathDismissKeyState: MutableState<String>,
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
                index = index,
                track = track,
                currentPlaylist = currentPlaylist,
                currentTrackIndex = currentTrackIndex,
                playlistDismissMediatorState,
                trackIndexDismissMediatorState,
                trackPathDismissKeyState
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
    index: Int,
    track: DefaultTrack,
    currentPlaylist: ImmutableList<DefaultTrack>,
    currentTrackIndex: Int,
    playlistDismissMediatorState: MutableState<ImmutableList<DefaultTrack>>,
    trackIndexDismissMediatorState: MutableState<Int>,
    trackPathDismissKeyState: MutableState<String>,
): Boolean {
    if (index == currentTrackIndex)
        return false

    playlistDismissMediatorState.value =
        (currentPlaylist.take(index) + currentPlaylist.drop(index + 1))
            .toImmutableList()

    trackIndexDismissMediatorState.value = index
    trackPathDismissKeyState.value = track.path
    return true
}

private suspend fun updateCurrentPlaylist(
    newTracks: ImmutableList<DefaultTrack>,
    newCurTrackIndex: Int,
    viewModel: CurrentPlaylistViewModel,
    trackServiceAccessor: TrackServiceAccessor
) {
    viewModel.setCurrentPlaylist(newTracks)
    viewModel.setCurrentTrackIndex(newCurTrackIndex)
    trackServiceAccessor.updatePlaylistAfterDrag(newTracks, newCurTrackIndex)
}