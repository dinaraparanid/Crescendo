package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.pitchFlow
import com.paranid5.crescendo.data.properties.storePitch
import kotlinx.coroutines.flow.Flow

interface PitchStateSubscriber {
    val pitchFlow: Flow<Float>
}

interface PitchStatePublisher {
    suspend fun setPitch(pitch: Float)
}

class PitchStateSubscriberImpl(private val storageRepository: StorageRepository) : PitchStateSubscriber {
    override val pitchFlow by lazy {
        storageRepository.pitchFlow
    }
}

class PitchStatePublisherImpl(private val storageRepository: StorageRepository) : PitchStatePublisher {
    override suspend fun setPitch(pitch: Float) =
        storageRepository.storePitch(pitch)
}