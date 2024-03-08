package com.paranid5.crescendo.presentation.main.trimmer.properties

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import kotlinx.coroutines.flow.map

inline val TrimmerViewModel.trackDurationInMillisFlow
    get() = trackState.map { it?.durationMillis ?: 0L }

inline val TrimmerViewModel.trackPathOrNullFlow
    get() = trackState.map { it?.path }

fun TrimmerViewModel.setTrackAndResetPositions(track: com.paranid5.crescendo.core.common.tracks.Track) {
    setTrack(track)
    setStartPosInMillis(0)
    setEndPosInMillis(track.durationMillis)
}