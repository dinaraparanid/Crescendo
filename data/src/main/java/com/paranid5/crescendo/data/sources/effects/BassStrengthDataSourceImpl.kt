package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.bassStrengthFlow
import com.paranid5.crescendo.data.properties.storeBassStrength
import com.paranid5.crescendo.domain.sources.effects.BassStrengthPublisher
import com.paranid5.crescendo.domain.sources.effects.BassStrengthSubscriber

class BassStrengthSubscriberImpl(private val storageRepository: StorageRepository) :
    BassStrengthSubscriber {
    override val bassStrengthFlow by lazy {
        storageRepository.bassStrengthFlow
    }
}

class BassStrengthPublisherImpl(private val storageRepository: StorageRepository) :
    BassStrengthPublisher {
    override suspend fun setBassStrength(bassStrength: Short) =
        storageRepository.storeBassStrength(bassStrength)
}