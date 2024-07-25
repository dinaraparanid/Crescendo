package com.paranid5.crescendo.domain.audio_effects

import kotlinx.coroutines.flow.Flow

interface PitchDataSource {
    val pitchFlow: Flow<Float>

    suspend fun setPitch(pitch: Float)
}
