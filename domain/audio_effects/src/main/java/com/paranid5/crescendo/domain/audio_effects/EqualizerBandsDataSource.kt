package com.paranid5.crescendo.domain.audio_effects

import kotlinx.coroutines.flow.Flow

interface EqualizerBandsDataSource {
    val equalizerBandsFlow: Flow<List<Short>>

    suspend fun updateEqualizerBands(bands: List<Short>)
}
