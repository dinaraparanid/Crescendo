package com.paranid5.crescendo.tracks.view_model

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.tracks.TrackOrder

sealed interface TracksUiIntent {

    sealed interface Lifecycle : TracksUiIntent {
        data object OnStart : Lifecycle
        data object OnStop : Lifecycle
        data object OnRefresh : Lifecycle
    }

    sealed interface Tracks : TracksUiIntent {
        data class TrackClick(
            val nextPlaylist: List<Track>,
            val nextTrackIndex: Int,
        ) : Tracks

        data class AddTrackToPlaylist(val track: Track) : Tracks
    }

    sealed interface UpdateState : TracksUiIntent {
        data class UpdateSearchQuery(val query: String) : UpdateState
        data class UpdateTrackOrder(val order: TrackOrder) : UpdateState
    }

    sealed interface ScreenEffect : TracksUiIntent {
        data class ShowTrimmer(val trackUri: String) : ScreenEffect
        data object ShowMetaEditor : ScreenEffect
        data object ClearBackResult : ScreenEffect
    }
}
