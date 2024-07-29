package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.AudioEffectsDataStore
import com.paranid5.crescendo.domain.audio_effects.SpeedDataSource

internal class SpeedDataSourceImpl(
    private val audioEffectsDataStore: AudioEffectsDataStore,
) : SpeedDataSource {
    override val speedFlow by lazy {
        audioEffectsDataStore.speedFlow
    }

    override suspend fun updateSpeed(speed: Float) =
        audioEffectsDataStore.storeSpeed(speed)
}
