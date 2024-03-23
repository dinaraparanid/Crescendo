package com.paranid5.crescendo.data.states.effects

import com.paranid5.crescendo.data.StorageRepository
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

class EqualizerBandsStateSubscriberImpl(private val storageRepository: StorageRepository) :
    EqualizerBandsStateSubscriber {
    override val equalizerBandsFlow by lazy {
        storageRepository.equalizerBandsFlow
    }
}

class EqualizerBandsStatePublisherImpl(private val storageRepository: StorageRepository) :
    EqualizerBandsStatePublisher {
    override suspend fun setEqualizerBands(bands: ImmutableList<Short>) =
        storageRepository.storeEqualizerBands(bands)
}