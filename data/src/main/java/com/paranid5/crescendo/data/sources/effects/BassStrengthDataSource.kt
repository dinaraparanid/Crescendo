package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.bassStrengthFlow
import com.paranid5.crescendo.data.properties.storeBassStrength
import kotlinx.coroutines.flow.Flow

interface BassStrengthStateSubscriber {
    val bassStrengthFlow: Flow<Short>
}

interface BassStrengthStatePublisher {
    suspend fun setBassStrength(bassStrength: Short)
}

class BassStrengthStateSubscriberImpl(private val storageRepository: StorageRepository) :
    BassStrengthStateSubscriber {
    override val bassStrengthFlow by lazy {
        storageRepository.bassStrengthFlow
    }
}

class BassStrengthStatePublisherImpl(private val storageRepository: StorageRepository) :
    BassStrengthStatePublisher {
    override suspend fun setBassStrength(bassStrength: Short) =
        storageRepository.storeBassStrength(bassStrength)
}