package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.speedFlow
import com.paranid5.crescendo.data.properties.storeSpeed
import com.paranid5.crescendo.domain.audio_effects.SpeedDataSource

internal class SpeedDataSourceImpl(
    private val dataStoreProvider: DataStoreProvider,
) : SpeedDataSource {
    override val speedFlow by lazy {
        dataStoreProvider.speedFlow
    }

    override suspend fun setSpeed(speed: Float) =
        dataStoreProvider.storeSpeed(speed)
}
