package com.paranid5.crescendo.feature.current_playlist.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistState
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistUiIntent

@Composable
internal fun CurrentPlaylistTrackList(
    state: CurrentPlaylistState,
    onUiIntent: (CurrentPlaylistUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (playlistState, _, _) = state
    val currentTrackIndex by rememberUpdatedState(playlistState.currentTrackIndex)

    DraggableTrackList(
        tracks = playlistState.playlist,
        currentTrackIndex = playlistState.currentTrackIndex,
        modifier = modifier,
        onTrackDismissed = { index, _ ->
            when (index) {
                currentTrackIndex -> false
                else -> {
                    onUiIntent(CurrentPlaylistUiIntent.Playlist.DismissTrack(index))
                    true
                }
            }
        },
        onTrackDragged = { newPlaylist, newCurrentTrackIndex ->
            onUiIntent(
                CurrentPlaylistUiIntent.Playlist.UpdateAfterDrag(
                    newPlaylist = newPlaylist,
                    newCurrentTrackIndex = newCurrentTrackIndex,
                )
            )
        },
        onTrackClick = {
            onUiIntent(CurrentPlaylistUiIntent.Playlist.StartPlaylistPlayback(trackIndex = it))
        },
        addToPlaylist = {
            onUiIntent(CurrentPlaylistUiIntent.Playlist.AddTrackToPlaylist(track = it))
        },
        showTrimmer = {
            onUiIntent(CurrentPlaylistUiIntent.Screen.ShowTrimmer(trackUri = it))
        },
        showMetaEditor = {
            onUiIntent(CurrentPlaylistUiIntent.Screen.ShowMetaEditor)
        },
    )
}
