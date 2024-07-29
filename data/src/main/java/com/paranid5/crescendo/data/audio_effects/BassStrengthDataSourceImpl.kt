package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.AudioEffectsDataStore
import com.paranid5.crescendo.domain.audio_effects.BassStrengthDataSource

internal class BassStrengthDataSourceImpl(
    private val audioEffectsDataStore: AudioEffectsDataStore,
) : BassStrengthDataSource {
    override val bassStrengthFlow by lazy {
        audioEffectsDataStore.bassStrengthFlow
    }

    override suspend fun updateBassStrength(bassStrength: Short) =
        audioEffectsDataStore.storeBassStrength(bassStrength)
}
