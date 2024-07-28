package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.equalizerBandsFlow
import com.paranid5.crescendo.data.properties.storeEqualizerBands
import com.paranid5.crescendo.domain.audio_effects.EqualizerBandsDataSource

internal class EqualizerBandsDataSourceImpl(
    private val dataStoreProvider: DataStoreProvider,
) : EqualizerBandsDataSource {
    override val equalizerBandsFlow by lazy {
        dataStoreProvider.equalizerBandsFlow
    }

    override suspend fun setEqualizerBands(bands: List<Short>) =
        dataStoreProvider.storeEqualizerBands(bands)
}
