package com.paranid5.crescendo.trimmer.presentation.properties

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import kotlinx.coroutines.flow.map

internal inline val TrimmerViewModel.trackDurationInMillisFlow
    get() = trackState.map { it?.durationMillis ?: 0L }

internal inline val TrimmerViewModel.trackPathOrNullFlow
    get() = trackState.map { it?.path }

internal fun TrimmerViewModel.setTrackAndResetPositions(track: Track) {
    setTrack(track)
    setStartPosInMillis(0)
    setEndPosInMillis(track.durationMillis)
}