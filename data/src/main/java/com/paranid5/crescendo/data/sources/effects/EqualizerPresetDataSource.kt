package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.equalizerPresetFlow
import com.paranid5.crescendo.data.properties.storeEqualizerPreset
import kotlinx.coroutines.flow.Flow

interface EqualizerPresetStateSubscriber {
    val equalizerPresetFlow: Flow<Short>
}

interface EqualizerPresetStatePublisher {
    suspend fun setEqualizerPreset(preset: Short)
}

class EqualizerPresetStateSubscriberImpl(private val storageRepository: StorageRepository) :
    EqualizerPresetStateSubscriber {
    override val equalizerPresetFlow by lazy {
        storageRepository.equalizerPresetFlow
    }
}

class EqualizerPresetStatePublisherImpl(private val storageRepository: StorageRepository) :
    EqualizerPresetStatePublisher {
    override suspend fun setEqualizerPreset(preset: Short) =
        storageRepository.storeEqualizerPreset(preset)
}