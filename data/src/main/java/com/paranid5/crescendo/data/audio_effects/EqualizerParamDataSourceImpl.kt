package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.equalizerParamFlow
import com.paranid5.crescendo.data.properties.storeEqualizerParam
import com.paranid5.crescendo.domain.audio_effects.EqualizerParamDataSource
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset

internal class EqualizerParamDataSourceImpl(
    private val storageRepository: StorageRepository,
) : EqualizerParamDataSource {
    override val equalizerParamFlow by lazy {
        storageRepository.equalizerParamFlow
    }

    override suspend fun setEqualizerParam(param: EqualizerBandsPreset) =
        storageRepository.storeEqualizerParam(param)
}
