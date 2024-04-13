package com.paranid5.crescendo.domain.sources.effects

import kotlinx.coroutines.flow.Flow

interface AudioEffectsEnabledSubscriber {
    val areAudioEffectsEnabledFlow: Flow<Boolean>
}

interface AudioEffectsEnabledPublisher {
    suspend fun setAudioEffectsEnabled(areAudioEffectsEnabled: Boolean)
}