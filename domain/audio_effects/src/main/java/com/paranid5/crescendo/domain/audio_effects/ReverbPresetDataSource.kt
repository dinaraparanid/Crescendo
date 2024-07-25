package com.paranid5.crescendo.domain.audio_effects

import kotlinx.coroutines.flow.Flow

interface ReverbPresetDataSource {
    val reverbPresetFlow: Flow<Short>

    suspend fun setReverbPreset(reverbPreset: Short)
}
