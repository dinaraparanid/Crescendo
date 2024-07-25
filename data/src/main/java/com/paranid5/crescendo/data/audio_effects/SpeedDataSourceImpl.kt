package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.speedFlow
import com.paranid5.crescendo.data.properties.storeSpeed
import com.paranid5.crescendo.domain.audio_effects.SpeedDataSource

internal class SpeedDataSourceImpl(
    private val storageRepository: StorageRepository,
) : SpeedDataSource {
    override val speedFlow by lazy {
        storageRepository.speedFlow
    }

    override suspend fun setSpeed(speed: Float) =
        storageRepository.storeSpeed(speed)
}
