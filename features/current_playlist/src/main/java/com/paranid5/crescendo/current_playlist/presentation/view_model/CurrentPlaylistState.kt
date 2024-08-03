package com.paranid5.crescendo.current_playlist.presentation.view_model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.paranid5.crescendo.ui.track.ui_state.TrackUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.parcelize.Parcelize

@Parcelize
@Immutable
data class CurrentPlaylistState(
    val playlistState: PlaylistState = PlaylistState(),
    val dismissState: DismissState = DismissState(),
) : Parcelable {
    @Parcelize
    @Immutable
    data class PlaylistState(
        val playlist: ImmutableList<TrackUiState> = persistentListOf(),
        val currentTrack: TrackUiState? = null,
        val currentTrackIndex: Int = 0,
        val playlistSize: Int = 0,
        val playlistDurationFormatted: String = "",
    ) : Parcelable

    @Parcelize
    @Immutable
    data class DismissState(
        val playlistDismissMediator: ImmutableList<TrackUiState> = persistentListOf(),
        val trackIndexDismissMediator: Int = 0,
        val trackPathDismissKey: String = "",
    ) : Parcelable
}
