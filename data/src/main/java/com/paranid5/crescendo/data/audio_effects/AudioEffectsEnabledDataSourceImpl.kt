package com.paranid5.crescendo.data.audio_effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.areAudioEffectsEnabledFlow
import com.paranid5.crescendo.data.properties.storeAudioEffectsEnabled
import com.paranid5.crescendo.domain.audio_effects.AudioEffectsEnabledDataSource

internal class AudioEffectsEnabledDataSourceImpl(
    private val storageRepository: StorageRepository,
) : AudioEffectsEnabledDataSource {
    override val areAudioEffectsEnabledFlow by lazy {
        storageRepository.areAudioEffectsEnabledFlow
    }

    override suspend fun setAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) =
        storageRepository.storeAudioEffectsEnabled(areAudioEffectsEnabled)
}
