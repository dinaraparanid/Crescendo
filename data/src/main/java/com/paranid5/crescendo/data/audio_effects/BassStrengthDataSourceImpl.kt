package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.bassStrengthFlow
import com.paranid5.crescendo.data.properties.storeBassStrength
import com.paranid5.crescendo.domain.audio_effects.BassStrengthDataSource

internal class BassStrengthDataSourceImpl(
    private val storageRepository: StorageRepository,
) : BassStrengthDataSource {
    override val bassStrengthFlow by lazy {
        storageRepository.bassStrengthFlow
    }

    override suspend fun setBassStrength(bassStrength: Short) =
        storageRepository.storeBassStrength(bassStrength)
}
