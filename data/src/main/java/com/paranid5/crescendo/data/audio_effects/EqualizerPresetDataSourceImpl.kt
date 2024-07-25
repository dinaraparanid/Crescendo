package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.equalizerPresetFlow
import com.paranid5.crescendo.data.properties.storeEqualizerPreset
import com.paranid5.crescendo.domain.audio_effects.EqualizerPresetDataSource

internal class EqualizerPresetDataSourceImpl(
    private val storageRepository: StorageRepository,
) : EqualizerPresetDataSource {
    override val equalizerPresetFlow by lazy {
        storageRepository.equalizerPresetFlow
    }

    override suspend fun setEqualizerPreset(preset: Short) =
        storageRepository.storeEqualizerPreset(preset)
}
