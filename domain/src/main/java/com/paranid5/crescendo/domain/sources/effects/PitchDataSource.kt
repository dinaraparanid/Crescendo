package com.paranid5.crescendo.domain.sources.effects

import kotlinx.coroutines.flow.Flow

interface PitchSubscriber {
    val pitchFlow: Flow<Float>
}

interface PitchPublisher {
    suspend fun setPitch(pitch: Float)
}