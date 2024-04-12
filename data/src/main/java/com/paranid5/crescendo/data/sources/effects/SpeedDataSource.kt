package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.speedFlow
import com.paranid5.crescendo.data.properties.storeSpeed
import kotlinx.coroutines.flow.Flow

interface SpeedStateSubscriber {
    val speedFlow: Flow<Float>
}

interface SpeedStatePublisher {
    suspend fun setSpeed(speed: Float)
}

class SpeedStateSubscriberImpl(private val storageRepository: StorageRepository) : SpeedStateSubscriber {
    override val speedFlow by lazy {
        storageRepository.speedFlow
    }
}

class SpeedStatePublisherImpl(private val storageRepository: StorageRepository) : SpeedStatePublisher {
    override suspend fun setSpeed(speed: Float) =
        storageRepository.storeSpeed(speed)
}