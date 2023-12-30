package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.pitchFlow
import com.paranid5.crescendo.data.properties.storePitch
import kotlinx.coroutines.flow.Flow

interface PitchStateSubscriber {
    val pitchFlow: Flow<Float>
}

interface PitchStatePublisher {
    suspend fun setPitch(pitch: Float)
}

class PitchStateSubscriberImpl(private val storageHandler: StorageHandler) : PitchStateSubscriber {
    override val pitchFlow by lazy {
        storageHandler.pitchFlow
    }
}

class PitchStatePublisherImpl(private val storageHandler: StorageHandler) : PitchStatePublisher {
    override suspend fun setPitch(pitch: Float) =
        storageHandler.storePitch(pitch)
}