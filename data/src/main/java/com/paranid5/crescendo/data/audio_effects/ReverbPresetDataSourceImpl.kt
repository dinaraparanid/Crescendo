package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.reverbPresetFlow
import com.paranid5.crescendo.data.properties.storeReverbPreset
import com.paranid5.crescendo.domain.audio_effects.ReverbPresetDataSource

internal class ReverbPresetDataSourceImpl(
    private val dataStoreProvider: DataStoreProvider,
) : ReverbPresetDataSource {
    override val reverbPresetFlow by lazy {
        dataStoreProvider.reverbPresetFlow
    }

    override suspend fun setReverbPreset(reverbPreset: Short) =
        dataStoreProvider.storeReverbPreset(reverbPreset)
}
