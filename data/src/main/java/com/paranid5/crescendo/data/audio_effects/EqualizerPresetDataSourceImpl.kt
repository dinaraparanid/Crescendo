package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.equalizerPresetFlow
import com.paranid5.crescendo.data.properties.storeEqualizerPreset
import com.paranid5.crescendo.domain.audio_effects.EqualizerPresetDataSource

internal class EqualizerPresetDataSourceImpl(
    private val dataStoreProvider: DataStoreProvider,
) : EqualizerPresetDataSource {
    override val equalizerPresetFlow by lazy {
        dataStoreProvider.equalizerPresetFlow
    }

    override suspend fun setEqualizerPreset(preset: Short) =
        dataStoreProvider.storeEqualizerPreset(preset)
}
