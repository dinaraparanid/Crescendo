package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.AudioEffectsDataStore
import com.paranid5.crescendo.domain.audio_effects.EqualizerParamDataSource
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset

internal class EqualizerParamDataSourceImpl(
    private val audioEffectsDataStore: AudioEffectsDataStore,
) : EqualizerParamDataSource {
    override val equalizerParamFlow by lazy {
        audioEffectsDataStore.equalizerParamFlow
    }

    override suspend fun updateEqualizerParam(param: EqualizerBandsPreset) =
        audioEffectsDataStore.storeEqualizerParam(param)
}
