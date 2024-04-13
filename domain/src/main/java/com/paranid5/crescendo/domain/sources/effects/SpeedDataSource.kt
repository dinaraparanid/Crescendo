package com.paranid5.crescendo.domain.sources.effects

import kotlinx.coroutines.flow.Flow

interface SpeedSubscriber {
    val speedFlow: Flow<Float>
}

interface SpeedPublisher {
    suspend fun setSpeed(speed: Float)
}