package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.areAudioEffectsEnabledFlow
import com.paranid5.crescendo.data.properties.storeAudioEffectsEnabled
import com.paranid5.crescendo.domain.sources.effects.AudioEffectsEnabledPublisher
import com.paranid5.crescendo.domain.sources.effects.AudioEffectsEnabledSubscriber

class AudioEffectsEnabledSubscriberImpl(private val storageRepository: StorageRepository) :
    AudioEffectsEnabledSubscriber {
    override val areAudioEffectsEnabledFlow by lazy {
        storageRepository.areAudioEffectsEnabledFlow
    }
}

class AudioEffectsEnabledPublisherImpl(private val storageRepository: StorageRepository) :
    AudioEffectsEnabledPublisher {
    override suspend fun setAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) =
        storageRepository.storeAudioEffectsEnabled(areAudioEffectsEnabled)
}