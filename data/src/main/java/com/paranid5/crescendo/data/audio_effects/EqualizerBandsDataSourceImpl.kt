package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.AudioEffectsDataStore
import com.paranid5.crescendo.domain.audio_effects.EqualizerBandsDataSource

internal class EqualizerBandsDataSourceImpl(
    private val audioEffectsDataStore: AudioEffectsDataStore,
) : EqualizerBandsDataSource {
    override val equalizerBandsFlow by lazy {
        audioEffectsDataStore.equalizerBandsFlow
    }

    override suspend fun updateEqualizerBands(bands: List<Short>) =
        audioEffectsDataStore.storeEqualizerBands(bands)
}
