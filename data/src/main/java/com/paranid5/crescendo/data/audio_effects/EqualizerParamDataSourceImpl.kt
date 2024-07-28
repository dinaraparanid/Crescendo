package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.equalizerParamFlow
import com.paranid5.crescendo.data.properties.storeEqualizerParam
import com.paranid5.crescendo.domain.audio_effects.EqualizerParamDataSource
import com.paranid5.crescendo.domain.audio_effects.entity.EqualizerBandsPreset

internal class EqualizerParamDataSourceImpl(
    private val dataStoreProvider: DataStoreProvider,
) : EqualizerParamDataSource {
    override val equalizerParamFlow by lazy {
        dataStoreProvider.equalizerParamFlow
    }

    override suspend fun setEqualizerParam(param: EqualizerBandsPreset) =
        dataStoreProvider.storeEqualizerParam(param)
}
