package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.equalizerPresetFlow
import com.paranid5.crescendo.data.properties.storeEqualizerPreset
import kotlinx.coroutines.flow.Flow

interface EqualizerPresetStateSubscriber {
    val equalizerPresetFlow: Flow<Short>
}

interface EqualizerPresetStatePublisher {
    suspend fun setEqualizerPreset(preset: Short)
}

class EqualizerPresetStateSubscriberImpl(private val storageHandler: StorageHandler) :
    EqualizerPresetStateSubscriber {
    override val equalizerPresetFlow by lazy {
        storageHandler.equalizerPresetFlow
    }
}

class EqualizerPresetStatePublisherImpl(private val storageHandler: StorageHandler) :
    EqualizerPresetStatePublisher {
    override suspend fun setEqualizerPreset(preset: Short) =
        storageHandler.storeEqualizerPreset(preset)
}