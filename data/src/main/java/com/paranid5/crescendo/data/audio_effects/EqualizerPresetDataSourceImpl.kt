package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.AudioEffectsDataStore
import com.paranid5.crescendo.domain.audio_effects.EqualizerPresetDataSource

internal class EqualizerPresetDataSourceImpl(
    private val audioEffectsDataStore: AudioEffectsDataStore,
) : EqualizerPresetDataSource {
    override val equalizerPresetFlow by lazy {
        audioEffectsDataStore.equalizerPresetFlow
    }

    override suspend fun updateEqualizerPreset(preset: Short) =
        audioEffectsDataStore.storeEqualizerPreset(preset)
}
