package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.equalizerBandsFlow
import com.paranid5.crescendo.data.properties.storeEqualizerBands
import com.paranid5.crescendo.domain.audio_effects.EqualizerBandsDataSource

internal class EqualizerBandsDataSourceImpl(
    private val storageRepository: StorageRepository,
) : EqualizerBandsDataSource {
    override val equalizerBandsFlow by lazy {
        storageRepository.equalizerBandsFlow
    }

    override suspend fun setEqualizerBands(bands: List<Short>) =
        storageRepository.storeEqualizerBands(bands)
}
