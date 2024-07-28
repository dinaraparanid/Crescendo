package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.pitchFlow
import com.paranid5.crescendo.data.properties.storePitch
import com.paranid5.crescendo.domain.audio_effects.PitchDataSource

internal class PitchDataSourceImpl(
    private val dataStoreProvider: DataStoreProvider,
) : PitchDataSource {
    override val pitchFlow by lazy {
        dataStoreProvider.pitchFlow
    }

    override suspend fun setPitch(pitch: Float) =
        dataStoreProvider.storePitch(pitch)
}
