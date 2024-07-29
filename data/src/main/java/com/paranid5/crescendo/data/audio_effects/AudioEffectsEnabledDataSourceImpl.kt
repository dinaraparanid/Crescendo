package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.AudioEffectsDataStore
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsEnabledDataSource

internal class AudioEffectsEnabledDataSourceImpl(
    private val audioEffectsDataStore: AudioEffectsDataStore,
) : AudioEffectsEnabledDataSource {
    override val areAudioEffectsEnabledFlow by lazy {
        audioEffectsDataStore.areAudioEffectsEnabledFlow
    }

    override suspend fun updateAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) =
        audioEffectsDataStore.storeAudioEffectsEnabled(areAudioEffectsEnabled)
}
