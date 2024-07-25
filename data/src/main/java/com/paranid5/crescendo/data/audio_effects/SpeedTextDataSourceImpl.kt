package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.speedTextFlow
import com.paranid5.crescendo.domain.audio_effects.SpeedTextDataSource

internal class SpeedTextDataSourceImpl(
    private val storageRepository: StorageRepository,
) : SpeedTextDataSource {
    override val speedTextState by lazy {
        storageRepository.speedTextFlow
    }
}
