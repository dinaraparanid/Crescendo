package com.paranid5.crescendo.domain.audio_effects

import kotlinx.coroutines.flow.Flow

interface SpeedDataSource {
    val speedFlow: Flow<Float>

    suspend fun updateSpeed(speed: Float)
}
