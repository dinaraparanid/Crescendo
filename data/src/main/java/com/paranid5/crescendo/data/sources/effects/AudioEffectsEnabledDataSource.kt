package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.areAudioEffectsEnabledFlow
import com.paranid5.crescendo.data.properties.storeAudioEffectsEnabled
import kotlinx.coroutines.flow.Flow

interface AudioEffectsEnabledStateSubscriber {
    val areAudioEffectsEnabledFlow: Flow<Boolean>
}

interface AudioEffectsEnabledStatePublisher {
    suspend fun setAudioEffectsEnabled(areAudioEffectsEnabled: Boolean)
}

class AudioEffectsEnabledStateSubscriberImpl(private val storageRepository: StorageRepository) :
    AudioEffectsEnabledStateSubscriber {
    override val areAudioEffectsEnabledFlow by lazy {
        storageRepository.areAudioEffectsEnabledFlow
    }
}

class AudioEffectsEnabledStatePublisherImpl(private val storageRepository: StorageRepository) :
    AudioEffectsEnabledStatePublisher {
    override suspend fun setAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) =
        storageRepository.storeAudioEffectsEnabled(areAudioEffectsEnabled)
}