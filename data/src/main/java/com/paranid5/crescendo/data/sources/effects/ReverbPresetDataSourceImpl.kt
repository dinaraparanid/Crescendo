package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.reverbPresetFlow
import com.paranid5.crescendo.data.properties.storeReverbPreset
import com.paranid5.crescendo.domain.sources.effects.ReverbPresetPublisher
import com.paranid5.crescendo.domain.sources.effects.ReverbPresetSubscriber

class ReverbPresetSubscriberImpl(private val storageRepository: StorageRepository) :
    ReverbPresetSubscriber {
    override val reverbPresetFlow by lazy {
        storageRepository.reverbPresetFlow
    }
}

class ReverbPresetPublisherImpl(private val storageRepository: StorageRepository) :
    ReverbPresetPublisher {
    override suspend fun setReverbPreset(reverbPreset: Short) =
        storageRepository.storeReverbPreset(reverbPreset)
}