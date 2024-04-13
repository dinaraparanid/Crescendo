package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.speedFlow
import com.paranid5.crescendo.data.properties.storeSpeed
import com.paranid5.crescendo.domain.sources.effects.SpeedPublisher
import com.paranid5.crescendo.domain.sources.effects.SpeedSubscriber

class SpeedSubscriberImpl(private val storageRepository: StorageRepository) : SpeedSubscriber {
    override val speedFlow by lazy {
        storageRepository.speedFlow
    }
}

class SpeedPublisherImpl(private val storageRepository: StorageRepository) : SpeedPublisher {
    override suspend fun setSpeed(speed: Float) =
        storageRepository.storeSpeed(speed)
}