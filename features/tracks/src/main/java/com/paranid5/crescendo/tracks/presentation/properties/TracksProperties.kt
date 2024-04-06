package com.paranid5.crescendo.tracks.presentation.properties

import com.paranid5.crescendo.core.common.tracks.sortedBy
import com.paranid5.crescendo.tracks.TracksViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

internal inline val TracksViewModel.shownTracksFlow
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

internal inline val TracksViewModel.shownTracksNumberFlow
    get() = shownTracksFlow.map { it.size }