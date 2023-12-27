package com.paranid5.crescendo.presentation.main.current_playlist.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.domain.tracks.DefaultTrack
import com.paranid5.crescendo.presentation.main.current_playlist.CurrentPlaylistViewModel
import com.paranid5.crescendo.services.track_service.TrackServiceAccessor
import org.koin.compose.koinInject

@Composable
fun CurrentPlaylistTrackList(
    playlistDismissMediatorState: MutableState<List<DefaultTrack>>,
    trackIndexDismissMediatorState: MutableState<Int>,
    trackPathDismissKeyState: MutableState<String>,
    viewModel: CurrentPlaylistViewModel,
    modifier: Modifier = Modifier,
    trackServiceAccessor: TrackServiceAccessor = koinInject()
) {
    val currentPlaylist by viewModel
        .currentPlaylistFlow
        .collectAsState(initial = emptyList())

    val currentTrackIndex by viewModel
        .currentTrackIndexFlow
        .collectAsState(initial = 0)

    DraggableTrackList(
        tracks = currentPlaylist,
        modifier = modifier,
        viewModel = viewModel,
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
    currentPlaylist: List<DefaultTrack>,
    currentTrackIndex: Int,
    playlistDismissMediatorState: MutableState<List<DefaultTrack>>,
    trackIndexDismissMediatorState: MutableState<Int>,
    trackPathDismissKeyState: MutableState<String>,
): Boolean {
    if (index == currentTrackIndex)
        return false

    playlistDismissMediatorState.value =
        currentPlaylist.take(index) + currentPlaylist.drop(index + 1)

    trackIndexDismissMediatorState.value = index
    trackPathDismissKeyState.value = track.path
    return true
}

private suspend fun updateCurrentPlaylist(
    newTracks: List<DefaultTrack>,
    newCurTrackIndex: Int,
    viewModel: CurrentPlaylistViewModel,
    trackServiceAccessor: TrackServiceAccessor
) {
    viewModel.storeCurrentPlaylist(newTracks)
    viewModel.storeCurrentTrackIndex(newCurTrackIndex)
    trackServiceAccessor.updatePlaylistAfterDrag(newTracks, newCurTrackIndex)
}