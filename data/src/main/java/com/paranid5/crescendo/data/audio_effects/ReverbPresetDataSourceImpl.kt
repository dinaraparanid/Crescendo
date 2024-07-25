package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.reverbPresetFlow
import com.paranid5.crescendo.data.properties.storeReverbPreset
import com.paranid5.crescendo.domain.audio_effects.ReverbPresetDataSource

internal class ReverbPresetDataSourceImpl(
    private val storageRepository: StorageRepository,
) : ReverbPresetDataSource {
    override val reverbPresetFlow by lazy {
        storageRepository.reverbPresetFlow
    }

    override suspend fun setReverbPreset(reverbPreset: Short) =
        storageRepository.storeReverbPreset(reverbPreset)
}
