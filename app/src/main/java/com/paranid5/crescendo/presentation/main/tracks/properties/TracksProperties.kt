package com.paranid5.crescendo.presentation.main.tracks.properties

import com.paranid5.crescendo.domain.tracks.sortedBy
import com.paranid5.crescendo.presentation.main.tracks.TracksViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

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