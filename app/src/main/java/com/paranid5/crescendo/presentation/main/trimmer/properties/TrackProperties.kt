package com.paranid5.crescendo.presentation.main.trimmer.properties

import com.paranid5.crescendo.domain.tracks.Track
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import kotlinx.coroutines.flow.map

inline val TrimmerViewModel.trackOrNullState
    get() = trackStateHolder.trackState

fun TrimmerViewModel.setTrack(track: Track) =
    trackStateHolder.setTrack(track, this)

inline val TrimmerViewModel.trackDurationInMillisFlow
    get() = trackOrNullState.map { it?.durationMillis ?: 0L }

inline val TrimmerViewModel.trackPathOrNullFlow
    get() = trackOrNullState.map { it?.path }