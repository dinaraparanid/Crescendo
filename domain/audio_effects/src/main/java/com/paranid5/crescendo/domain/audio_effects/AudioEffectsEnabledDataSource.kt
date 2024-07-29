package com.paranid5.crescendo.domain.audio_effects

import kotlinx.coroutines.flow.Flow

interface AudioEffectsEnabledDataSource {
    val areAudioEffectsEnabledFlow: Flow<Boolean>

    suspend fun updateAudioEffectsEnabled(areAudioEffectsEnabled: Boolean)
}
