package com.paranid5.crescendo.feature.current_playlist.view_model

import com.paranid5.crescendo.core.common.tracks.Track

interface CurrentPlaylistUiIntent {
    data object OnStart : CurrentPlaylistUiIntent

    data object OnStop : CurrentPlaylistUiIntent

    data class DismissTrack(val index: Int) : CurrentPlaylistUiIntent

    data class UpdateAfterDrag(
        val newPlaylist: List<Track>,
        val newCurrentTrackIndex: Int,
    ) : CurrentPlaylistUiIntent

    data class StartPlaylistPlayback(val trackIndex: Int) : CurrentPlaylistUiIntent

    data class ShowTrimmer(val trackUri: String) : CurrentPlaylistUiIntent
}
