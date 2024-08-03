package com.paranid5.crescendo.current_playlist.presentation.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import com.paranid5.crescendo.core.resources.ui.theme.AppTheme.dimensions
import com.paranid5.crescendo.current_playlist.presentation.view_model.CurrentPlaylistState
import com.paranid5.crescendo.current_playlist.presentation.view_model.CurrentPlaylistUiIntent

@Composable
internal fun CurrentPlaylistTrackList(
    state: CurrentPlaylistState,
    onUiIntent: (CurrentPlaylistUiIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val (playlistState, _) = state
    val currentTrackIndex by rememberUpdatedState(playlistState.currentTrackIndex)

    DraggableTrackList(
        tracks = playlistState.playlist,
        currentTrackIndex = playlistState.currentTrackIndex,
        modifier = modifier,
        bottomPadding = dimensions.padding.extraBig,
        onTrackDismissed = { index, _ ->
            when (index) {
                currentTrackIndex -> false
                else -> {
                    onUiIntent(CurrentPlaylistUiIntent.DismissTrack(index))
                    true
                }
            }
        },
        onTrackDragged = { newPlaylist, newCurrentTrackIndex ->
            onUiIntent(
                CurrentPlaylistUiIntent.UpdateAfterDrag(
                    newPlaylist = newPlaylist,
                    newCurrentTrackIndex = newCurrentTrackIndex,
                )
            )
        },
        onTrackClick = { trackIndex ->
            onUiIntent(CurrentPlaylistUiIntent.StartPlaylistPlayback(trackIndex = trackIndex))
        },
    )
}