package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.AudioEffectsDataStore
import com.paranid5.crescendo.domain.audio_effects.SpeedTextDataSource
import kotlinx.coroutines.flow.map

internal class SpeedTextDataSourceImpl(
    private val audioEffectsDataStore: AudioEffectsDataStore,
) : SpeedTextDataSource {
    override val speedTextFlow by lazy {
        audioEffectsDataStore.speedFlow.map(Float::toString)
    }
}
