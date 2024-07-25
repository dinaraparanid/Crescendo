package com.paranid5.crescendo.domain.audio_effects

import kotlinx.coroutines.flow.Flow

interface EqualizerPresetDataSource {
    val equalizerPresetFlow: Flow<Short>

    suspend fun setEqualizerPreset(preset: Short)
}
