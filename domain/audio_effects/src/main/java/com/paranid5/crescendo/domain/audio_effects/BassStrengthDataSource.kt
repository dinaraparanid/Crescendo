package com.paranid5.crescendo.domain.audio_effects

import kotlinx.coroutines.flow.Flow

interface BassStrengthDataSource {
    val bassStrengthFlow: Flow<Short>

    suspend fun updateBassStrength(bassStrength: Short)
}
