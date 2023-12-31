package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.equalizerBandsFlow
import com.paranid5.crescendo.data.properties.storeEqualizerBands
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface EqualizerBandsStateSubscriber {
    val equalizerBandsFlow: Flow<ImmutableList<Short>>
}

interface EqualizerBandsStatePublisher {
    suspend fun setEqualizerBands(bands: ImmutableList<Short>)
}

class EqualizerBandsStateSubscriberImpl(private val storageHandler: StorageHandler) :
    EqualizerBandsStateSubscriber {
    override val equalizerBandsFlow by lazy {
        storageHandler.equalizerBandsFlow
    }
}

class EqualizerBandsStatePublisherImpl(private val storageHandler: StorageHandler) :
    EqualizerBandsStatePublisher {
    override suspend fun setEqualizerBands(bands: ImmutableList<Short>) =
        storageHandler.storeEqualizerBands(bands)
}