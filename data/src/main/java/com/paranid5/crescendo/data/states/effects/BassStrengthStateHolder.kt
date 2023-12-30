package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.bassStrengthFlow
import com.paranid5.crescendo.data.properties.storeBassStrength
import kotlinx.coroutines.flow.Flow

interface BassStrengthStateSubscriber {
    val bassStrengthFlow: Flow<Short>
}

interface BassStrengthStatePublisher {
    suspend fun setBassStrength(bassStrength: Short)
}

class BassStrengthStateSubscriberImpl(private val storageHandler: StorageHandler) :
    BassStrengthStateSubscriber {
    override val bassStrengthFlow by lazy {
        storageHandler.bassStrengthFlow
    }
}

class BassStrengthStatePublisherImpl(private val storageHandler: StorageHandler) :
    BassStrengthStatePublisher {
    override suspend fun setBassStrength(bassStrength: Short) =
        storageHandler.storeBassStrength(bassStrength)
}