package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.reverbPresetFlow
import com.paranid5.crescendo.data.properties.storeReverbPreset
import kotlinx.coroutines.flow.Flow

interface ReverbPresetStateSubscriber {
    val reverbPresetFlow: Flow<Short>
}

interface ReverbPresetStatePublisher {
    suspend fun setReverbPreset(reverbPreset: Short)
}

class ReverbPresetStateSubscriberImpl(private val storageHandler: StorageHandler) :
    ReverbPresetStateSubscriber {
    override val reverbPresetFlow by lazy {
        storageHandler.reverbPresetFlow
    }
}

class ReverbPresetStatePublisherImpl(private val storageHandler: StorageHandler) :
    ReverbPresetStatePublisher {
    override suspend fun setReverbPreset(reverbPreset: Short) =
        storageHandler.storeReverbPreset(reverbPreset)
}