package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.equalizerBandsFlow
import com.paranid5.crescendo.data.properties.storeEqualizerBands
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface EqualizerBandsStateHolderSubscriber {
    val equalizerBandsFlow: Flow<ImmutableList<Short>>
}

interface EqualizerBandsStateHolderPublisher {
    suspend fun setEqualizerBands(bands: ImmutableList<Short>)
}

class EqualizerBandsStateHolderSubscriberImpl(private val storageHandler: StorageHandler) :
    EqualizerBandsStateHolderSubscriber {
    override val equalizerBandsFlow by lazy {
        storageHandler.equalizerBandsFlow
    }
}

class EqualizerBandsStateHolderPublisherImpl(private val storageHandler: StorageHandler) :
    EqualizerBandsStateHolderPublisher {
    override suspend fun setEqualizerBands(bands: ImmutableList<Short>) =
        storageHandler.storeEqualizerBands(bands)
}