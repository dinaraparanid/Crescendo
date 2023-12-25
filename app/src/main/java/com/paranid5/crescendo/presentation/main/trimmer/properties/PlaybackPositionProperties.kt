package com.paranid5.crescendo.presentation.main.trimmer.properties

import com.paranid5.crescendo.domain.trimming.FadeDurations
import com.paranid5.crescendo.domain.trimming.TrimRange
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

inline val TrimmerViewModel.fadeInSecsState
    get() = playbackPositionStateHolder.fadeInSecsState

fun TrimmerViewModel.setFadeInSecs(fadeInSecs: Long) =
    playbackPositionStateHolder.setFadeInSecs(fadeInSecs)

inline val TrimmerViewModel.fadeOutSecsState
    get() = playbackPositionStateHolder.fadeOutSecsState

fun TrimmerViewModel.setFadeOutSecs(fadeOutSecs: Long) =
    playbackPositionStateHolder.setFadeOutSecs(fadeOutSecs)

inline val TrimmerViewModel.startOffsetFlow
    get() = combine(
        startPosInMillisState,
        trackDurationInMillisFlow
    ) { startMillis, durationInMillis ->
        startMillis safeDiv durationInMillis
    }

inline val TrimmerViewModel.endOffsetFlow
    get() = combine(
        endPosInMillisState,
        trackDurationInMillisFlow
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

inline val TrimmerViewModel.trimRangeFlow
    get() = combine(
        startPosInMillisState,
        trimmedDurationInMillisFlow
    ) { startMillis, trimmedDurationMillis ->
        TrimRange(
            startPointMillis = startMillis,
            totalDurationMillis = trimmedDurationMillis
        )
    }

inline val TrimmerViewModel.fadeDurationsFlow
    get() = combine(
        fadeInSecsState,
        fadeOutSecsState
    ) { fadeInSecs, fadeOutSecs ->
        FadeDurations(
            fadeInSecs = fadeInSecs,
            fadeOutSecs = fadeOutSecs
        )
    }

inline val TrimmerViewModel.playbackOffsetFlow
    get() = combine(
        playbackPosInMillisState,
        trackDurationInMillisFlow
    ) { playbackMillis, durationInMillis ->
        playbackMillis safeDiv durationInMillis
    }

inline val TrimmerViewModel.playbackTextFlow
    get() = playbackPosInMillisState.map { it.timeString }

fun TrimmerViewModel.waveformWidthFlow(spikeWidthRatio: Int) =
    combine(
        trackDurationInMillisFlow,
        zoomState,
        zoomStepsState
    ) { durationMillis, zoom, zoomSteps ->
        (durationMillis / 1000 * spikeWidthRatio / (1 shl (zoomSteps - zoom))).toInt()
    }

fun TrimmerViewModel.waveformMaxWidthFlow(spikeWidthRatio: Int) =
    trackDurationInMillisFlow.map { (it / 1000 * spikeWidthRatio).toInt() }

fun TrimmerViewModel.playbackControllerOffsetFlow(spikeWidthRatio: Int) =
    combine(
        playbackOffsetFlow,
        waveformWidthFlow(spikeWidthRatio)
    ) { playbackOffset, waveformWidth ->
        CONTROLLER_CIRCLE_CENTER / 2 +
                playbackOffset * (waveformWidth - CONTROLLER_CIRCLE_RADIUS - CONTROLLER_RECT_OFFSET) +
                CONTROLLER_RECT_OFFSET
    }