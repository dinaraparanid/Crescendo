package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.equalizerPresetFlow
import com.paranid5.crescendo.data.properties.storeEqualizerPreset
import com.paranid5.crescendo.domain.sources.effects.EqualizerPresetPublisher
import com.paranid5.crescendo.domain.sources.effects.EqualizerPresetSubscriber

class EqualizerPresetStateSubscriberImpl(private val storageRepository: StorageRepository) :
    EqualizerPresetSubscriber {
    override val equalizerPresetFlow by lazy {
        storageRepository.equalizerPresetFlow
    }
}

class EqualizerPresetStatePublisherImpl(private val storageRepository: StorageRepository) :
    EqualizerPresetPublisher {
    override suspend fun setEqualizerPreset(preset: Short) =
        storageRepository.storeEqualizerPreset(preset)
}