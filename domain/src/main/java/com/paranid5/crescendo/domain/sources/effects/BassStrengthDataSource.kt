package com.paranid5.crescendo.domain.sources.effects

import kotlinx.coroutines.flow.Flow

interface BassStrengthSubscriber {
    val bassStrengthFlow: Flow<Short>
}

interface BassStrengthPublisher {
    suspend fun setBassStrength(bassStrength: Short)
}