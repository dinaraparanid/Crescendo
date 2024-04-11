package com.paranid5.crescendo.trimmer.presentation.properties

import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

internal inline val TrimmerViewModel.playbackAlphaFlow
    get() = isPlayingState.map { if (it) 1F else 0F }

internal inline val TrimmerViewModel.pitchAndSpeedFlow
    get() = combine(pitchState, speedState) { pitch, speed ->
        PitchAndSpeed(pitch, speed)
    }