package com.paranid5.crescendo.presentation.main.trimmer.properties

import com.paranid5.crescendo.domain.caching.CacheTrimRange
import com.paranid5.crescendo.domain.utils.extensions.timeString
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_CIRCLE_CENTER
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_CIRCLE_RADIUS
import com.paranid5.crescendo.presentation.main.trimmer.CONTROLLER_RECT_OFFSET
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.ui.extensions.safeDiv
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

inline val TrimmerViewModel.startPosInMillisState
    get() = playbackPositionStateHolder.startPosInMillisState

fun TrimmerViewModel.setStartPosInMillis(startMillis: Long) =
    playbackPositionStateHolder.setStartPosInMillis(startMillis)

inline val TrimmerViewModel.endPosInMillisState
    get() = playbackPositionStateHolder.endPosInMillisState

fun TrimmerViewModel.setEndPosInMillis(endMillis: Long) =
    playbackPositionStateHolder.setEndPosInMillis(endMillis)

inline val TrimmerViewModel.playbackPosInMillisState
    get() = playbackPositionStateHolder.playbackPosInMillisState

fun TrimmerViewModel.setPlaybackPosInMillis(position: Long) =
    playbackPositionStateHolder.setPlaybackPosInMillis(position)

inline val TrimmerViewModel.startOffsetFlow
    get() = combine(startPosInMillisState, durationInMillisFlow) { startMillis, durationInMillis ->
        startMillis safeDiv durationInMillis
    }

inline val TrimmerViewModel.endOffsetFlow
    get() = combine(
        endPosInMillisState,
        durationInMillisFlow
    ) { endMillis, durationInMillis ->
        endMillis safeDiv durationInMillis
    }

inline val TrimmerViewModel.trimmedDurationInMillisFlow
    get() = combine(
        startPosInMillisState,
        endPosInMillisState
    ) { startMillis, endMillis ->
        endMillis - startMillis
    }

inline val TrimmerViewModel.cacheTrimRangeFlow
    get() = combine(
        startPosInMillisState,
        trimmedDurationInMillisFlow
    ) { startMillis, trimmedDurationMillis ->
        CacheTrimRange(
            startPointSecs = startMillis / 1000,
            totalDurationSecs = trimmedDurationMillis / 1000
        )
    }

inline val TrimmerViewModel.playbackOffsetFlow
    get() = combine(
        playbackPosInMillisState,
        durationInMillisFlow
    ) { playbackMillis, durationInMillis ->
        playbackMillis safeDiv durationInMillis
    }

inline val TrimmerViewModel.playbackTextFlow
    get() = playbackPosInMillisState.map { it.timeString }

fun TrimmerViewModel.waveformWidthFlow(spikeWidthRatio: Int) =
    durationInMillisFlow.map { (it / 1000 * spikeWidthRatio).toInt() }

fun TrimmerViewModel.playbackControllerOffsetFlow(spikeWidthRatio: Int) =
    combine(
        playbackOffsetFlow,
        waveformWidthFlow(spikeWidthRatio)
    ) { playbackOffset, waveformWidth ->
        CONTROLLER_CIRCLE_CENTER / 2 +
                playbackOffset * (waveformWidth - CONTROLLER_CIRCLE_RADIUS - CONTROLLER_RECT_OFFSET) +
                CONTROLLER_RECT_OFFSET
    }