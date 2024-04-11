package com.paranid5.crescendo.trimmer.presentation.properties

import com.paranid5.crescendo.core.common.trimming.FadeDurations
import com.paranid5.crescendo.core.common.trimming.TrimRange
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_CIRCLE_CENTER
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_CIRCLE_RADIUS
import com.paranid5.crescendo.trimmer.presentation.CONTROLLER_RECT_OFFSET
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import com.paranid5.crescendo.utils.extensions.safeDiv
import com.paranid5.crescendo.utils.extensions.timeString
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

internal inline val TrimmerViewModel.startOffsetFlow
    get() = combine(
        startPosInMillisState,
        trackDurationInMillisFlow
    ) { startMillis, durationInMillis ->
        startMillis safeDiv durationInMillis
    }

internal inline val TrimmerViewModel.endOffsetFlow
    get() = combine(
        endPosInMillisState,
        trackDurationInMillisFlow
    ) { endMillis, durationInMillis ->
        endMillis safeDiv durationInMillis
    }

internal inline val TrimmerViewModel.trimmedDurationInMillisFlow
    get() = combine(
        startPosInMillisState,
        endPosInMillisState
    ) { startMillis, endMillis ->
        endMillis - startMillis
    }

internal inline val TrimmerViewModel.trimRangeFlow
    get() = combine(
        startPosInMillisState,
        trimmedDurationInMillisFlow
    ) { startMillis, trimmedDurationMillis ->
        TrimRange(
            startPointMillis = startMillis,
            totalDurationMillis = trimmedDurationMillis
        )
    }

internal inline val TrimmerViewModel.fadeDurationsFlow
    get() = combine(
        fadeInSecsState,
        fadeOutSecsState
    ) { fadeInSecs, fadeOutSecs ->
        FadeDurations(
            fadeInSecs = fadeInSecs,
            fadeOutSecs = fadeOutSecs
        )
    }

internal inline val TrimmerViewModel.playbackOffsetFlow
    get() = combine(
        playbackPosInMillisState,
        trackDurationInMillisFlow
    ) { playbackMillis, durationInMillis ->
        playbackMillis safeDiv durationInMillis
    }

internal inline val TrimmerViewModel.playbackTextFlow
    get() = playbackPosInMillisState.map { it.timeString }

internal fun TrimmerViewModel.playbackControllerOffsetFlow(spikeWidthRatio: Int) =
    combine(
        playbackOffsetFlow,
        waveformWidthFlow(spikeWidthRatio)
    ) { playbackOffset, waveformWidth ->
        CONTROLLER_CIRCLE_CENTER / 2 +
                playbackOffset * (waveformWidth - CONTROLLER_CIRCLE_RADIUS - CONTROLLER_RECT_OFFSET) +
                CONTROLLER_RECT_OFFSET
    }