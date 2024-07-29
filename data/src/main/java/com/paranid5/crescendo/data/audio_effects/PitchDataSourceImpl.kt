package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.AudioEffectsDataStore
import com.paranid5.crescendo.domain.audio_effects.PitchDataSource

internal class PitchDataSourceImpl(
    private val audioEffectsDataStore: AudioEffectsDataStore,
) : PitchDataSource {
    override val pitchFlow by lazy {
        audioEffectsDataStore.pitchFlow
    }

    override suspend fun updatePitch(pitch: Float) =
        audioEffectsDataStore.storePitch(pitch)
}
