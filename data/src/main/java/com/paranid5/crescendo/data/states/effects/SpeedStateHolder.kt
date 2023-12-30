package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.speedFlow
import com.paranid5.crescendo.data.properties.storeSpeed
import kotlinx.coroutines.flow.Flow

interface SpeedStateSubscriber {
    val speedFlow: Flow<Float>
}

interface SpeedStatePublisher {
    suspend fun setSpeed(speed: Float)
}

class SpeedStateSubscriberImpl(private val storageHandler: StorageHandler) : SpeedStateSubscriber {
    override val speedFlow by lazy {
        storageHandler.speedFlow
    }
}

class SpeedStatePublisherImpl(private val storageHandler: StorageHandler) : SpeedStatePublisher {
    override suspend fun setSpeed(speed: Float) =
        storageHandler.storeSpeed(speed)
}