package com.paranid5.crescendo.feature.current_playlist.view_model

import com.paranid5.crescendo.core.common.tracks.Track

interface CurrentPlaylistUiIntent {

    sealed interface Lifecycle : CurrentPlaylistUiIntent {
        data object OnStart : Lifecycle
        data object OnStop : Lifecycle
    }

    sealed interface Playlist : CurrentPlaylistUiIntent {
        data class StartPlaylistPlayback(val trackIndex: Int) : Playlist

        data class DismissTrack(val index: Int) : Playlist

        data class UpdateAfterDrag(
            val newPlaylist: List<Track>,
            val newCurrentTrackIndex: Int,
        ) : Playlist

        data class AddTrackToPlaylist(val track: Track) : Playlist
    }

    sealed interface Screen : CurrentPlaylistUiIntent {
        data class ShowTrimmer(val trackUri: String) : Screen
        data object ShowMetaEditor : Screen
        data object ClearScreenEffect : Screen
    }
}
