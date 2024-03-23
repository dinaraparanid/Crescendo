package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.reverbPresetFlow
import com.paranid5.crescendo.data.properties.storeReverbPreset
import kotlinx.coroutines.flow.Flow

interface ReverbPresetStateSubscriber {
    val reverbPresetFlow: Flow<Short>
}

interface ReverbPresetStatePublisher {
    suspend fun setReverbPreset(reverbPreset: Short)
}

class ReverbPresetStateSubscriberImpl(private val storageRepository: StorageRepository) :
    ReverbPresetStateSubscriber {
    override val reverbPresetFlow by lazy {
        storageRepository.reverbPresetFlow
    }
}

class ReverbPresetStatePublisherImpl(private val storageRepository: StorageRepository) :
    ReverbPresetStatePublisher {
    override suspend fun setReverbPreset(reverbPreset: Short) =
        storageRepository.storeReverbPreset(reverbPreset)
}