package com.paranid5.crescendo.domain.audio_effects

import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerData
import kotlinx.coroutines.flow.StateFlow

interface EqualizerStateHolder {
    val equalizerState: StateFlow<EqualizerData?>

    fun updateEqualizerData(equalizerData: EqualizerData?)
}