package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.pitchTextFlow
import com.paranid5.crescendo.domain.audio_effects.PitchTextDataSource

internal class PitchTextDataSourceImpl(
    private val storageRepository: StorageRepository,
) : PitchTextDataSource {
    override val pitchTextFlow by lazy {
        storageRepository.pitchTextFlow
    }
}
