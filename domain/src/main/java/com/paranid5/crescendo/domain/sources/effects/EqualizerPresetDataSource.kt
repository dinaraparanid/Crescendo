package com.paranid5.crescendo.domain.sources.effects

import kotlinx.coroutines.flow.Flow

interface EqualizerPresetSubscriber {
    val equalizerPresetFlow: Flow<Short>
}

interface EqualizerPresetPublisher {
    suspend fun setEqualizerPreset(preset: Short)
}