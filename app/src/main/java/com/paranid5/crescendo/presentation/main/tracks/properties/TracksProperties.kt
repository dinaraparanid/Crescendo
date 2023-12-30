package com.paranid5.crescendo.presentation.main.tracks.properties

import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.domain.tracks.TrackOrder
import com.paranid5.crescendo.domain.tracks.sortedBy
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

inline val TracksViewModel.tracksState
    get() = tracksStateHolder.tracksState

fun TracksViewModel.setTracks(tracks: ImmutableList<Track>) =
    tracksStateHolder.setTracks(tracks)

inline val TracksViewModel.filteredTracksState
    get() = tracksStateHolder.filteredTracksState

fun TracksViewModel.setFilteredTracks(tracks: ImmutableList<Track>) =
    tracksStateHolder.setFilteredTracks(tracks)

inline val TracksViewModel.trackOrderFlow
    get() = tracksStateHolder.trackOrderFlow

suspend inline fun TracksViewModel.setTrackOrder(trackOrder: TrackOrder) =
    tracksStateHolder.setTrackOrder(trackOrder)

inline val TracksViewModel.currentTrackFlow
    get() = tracksStateHolder.currentTrackFlow

inline val TracksViewModel.shownTracksFlow
    get() = combine(
        tracksState,
        filteredTracksState,
        trackOrderFlow,
        isSearchBarActiveState
    ) { tracks, filteredTracks, trackOrder, isSearchBarActive ->
        when {
            isSearchBarActive -> filteredTracks sortedBy trackOrder
            else -> tracks sortedBy trackOrder
        }
    }

inline val TracksViewModel.shownTracksNumberFlow
    get() = shownTracksFlow.map { it.size }