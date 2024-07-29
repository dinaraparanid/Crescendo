package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.datastore.AudioEffectsDataStore
import com.paranid5.crescendo.domain.audio_effects.PitchTextDataSource
import kotlinx.coroutines.flow.map

internal class PitchTextDataSourceImpl(
    private val audioEffectsDataStore: AudioEffectsDataStore,
) : PitchTextDataSource {
    override val pitchTextFlow by lazy {
        audioEffectsDataStore.pitchFlow.map(Float::toString)
    }
}
