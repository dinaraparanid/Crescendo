package com.paranid5.crescendo.tracks.view_model

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.core.common.tracks.TrackOrder

sealed interface TracksUiIntent {

    data object OnStart : TracksUiIntent

    data object OnStop : TracksUiIntent

    data object OnRefresh : TracksUiIntent

    data class UpdateSearchQuery(val query: String) : TracksUiIntent

    data class UpdateTrackOrder(val order: TrackOrder) : TracksUiIntent

    data class TrackClick(
        val nextPlaylist: List<Track>,
        val nextTrackIndex: Int,
    ) : TracksUiIntent

    data class ShowTrimmer(val trackUri: String) : TracksUiIntent
}
