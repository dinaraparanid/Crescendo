package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.AudioEffectsDataStore
import com.paranid5.crescendo.domain.audio_effects.ReverbPresetDataSource

internal class ReverbPresetDataSourceImpl(
    private val audioEffectsDataStore: AudioEffectsDataStore,
) : ReverbPresetDataSource {
    override val reverbPresetFlow by lazy {
        audioEffectsDataStore.reverbPresetFlow
    }

    override suspend fun updateReverbPreset(reverbPreset: Short) =
        audioEffectsDataStore.storeReverbPreset(reverbPreset)
}
