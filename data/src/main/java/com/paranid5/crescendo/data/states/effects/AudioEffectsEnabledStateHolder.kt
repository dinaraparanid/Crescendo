package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.areAudioEffectsEnabledFlow
import com.paranid5.crescendo.data.properties.storeAudioEffectsEnabled
import kotlinx.coroutines.flow.Flow

interface AudioEffectsEnabledStateSubscriber {
    val areAudioEffectsEnabledFlow: Flow<Boolean>
}

interface AudioEffectsEnabledStatePublisher {
    suspend fun setAudioEffectsEnabled(areAudioEffectsEnabled: Boolean)
}

class AudioEffectsEnabledStateSubscriberImpl(private val storageHandler: StorageHandler) :
    AudioEffectsEnabledStateSubscriber {
    override val areAudioEffectsEnabledFlow by lazy {
        storageHandler.areAudioEffectsEnabledFlow
    }
}

class AudioEffectsEnabledStatePublisherImpl(private val storageHandler: StorageHandler) :
    AudioEffectsEnabledStatePublisher {
    override suspend fun setAudioEffectsEnabled(areAudioEffectsEnabled: Boolean) =
        storageHandler.storeAudioEffectsEnabled(areAudioEffectsEnabled)
}