package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.speedTextFlow
import com.paranid5.crescendo.domain.audio_effects.SpeedTextDataSource

internal class SpeedTextDataSourceImpl(
    private val dataStoreProvider: DataStoreProvider,
) : SpeedTextDataSource {
    override val speedTextState by lazy {
        dataStoreProvider.speedTextFlow
    }
}
