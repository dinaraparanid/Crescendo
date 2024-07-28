package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.pitchTextFlow
import com.paranid5.crescendo.domain.audio_effects.PitchTextDataSource

internal class PitchTextDataSourceImpl(
    private val dataStoreProvider: DataStoreProvider,
) : PitchTextDataSource {
    override val pitchTextFlow by lazy {
        dataStoreProvider.pitchTextFlow
    }
}
