package com.paranid5.crescendo.domain.sources.effects

import kotlinx.coroutines.flow.Flow

interface ReverbPresetSubscriber {
    val reverbPresetFlow: Flow<Short>
}

interface ReverbPresetPublisher {
    suspend fun setReverbPreset(reverbPreset: Short)
}