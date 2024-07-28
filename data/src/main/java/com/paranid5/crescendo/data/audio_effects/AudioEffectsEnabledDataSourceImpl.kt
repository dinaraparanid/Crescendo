package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.areAudioEffectsEnabledFlow
import com.paranid5.crescendo.data.properties.storeAudioEffectsEnabled
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsEnabledDataSource

internal class AudioEffectsEnabledDataSourceImpl(
    private val dataStoreProvider: DataStoreProvider,
) : AudioEffectsEnabledDataSource {
    override val areAudioEffectsEnabledFlow by lazy {
        dataStoreProvider.areAudioEffectsEnabledFlow
    }

    override suspend fun setAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) =
        dataStoreProvider.storeAudioEffectsEnabled(areAudioEffectsEnabled)
}
