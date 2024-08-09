package com.paranid5.crescendo.feature.current_playlist.view_model

import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class CurrentPlaylistState(
    val playlistState: PlaylistState = PlaylistState(),
    val dismissState: DismissState = DismissState(),
    val screenEffect: CurrentPlaylistScreenEffect? = null,
) {

    @Immutable
    data class PlaylistState(
        val playlist: ImmutableList<TrackUiState> = persistentListOf(),
        val currentTrack: TrackUiState? = null,
        val currentTrackIndex: Int = 0,
        val playlistSize: Int = 0,
        val playlistDurationFormatted: String = "",
    )

    @Immutable
    data class DismissState(
        val playlistDismissMediator: ImmutableList<TrackUiState> = persistentListOf(),
        val trackIndexDismissMediator: Int = 0,
        val trackPathDismissKey: String = "",
    )
}
