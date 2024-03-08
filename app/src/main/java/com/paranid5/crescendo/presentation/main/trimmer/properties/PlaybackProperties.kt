package com.paranid5.crescendo.presentation.main.trimmer.properties

import com.paranid5.crescendo.core.common.trimming.PitchAndSpeed
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

inline val TrimmerViewModel.playbackAlphaFlow
    get() = isPlayingState.map { if (it) 1F else 0F }

inline val TrimmerViewModel.pitchAndSpeedFlow
    get() = combine(pitchState, speedState) { pitch, speed ->
        com.paranid5.crescendo.core.common.trimming.PitchAndSpeed(pitch, speed)
    }