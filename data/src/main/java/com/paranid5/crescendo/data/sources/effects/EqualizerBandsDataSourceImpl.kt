package com.paranid5.crescendo.data.sources.effects

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.equalizerBandsFlow
import com.paranid5.crescendo.data.properties.storeEqualizerBands
import com.paranid5.crescendo.domain.sources.effects.EqualizerBandsPublisher
import com.paranid5.crescendo.domain.sources.effects.EqualizerBandsSubscriber
import kotlinx.collections.immutable.ImmutableList

class EqualizerBandsSubscriberImpl(private val storageRepository: StorageRepository) :
    EqualizerBandsSubscriber {
    override val equalizerBandsFlow by lazy {
        storageRepository.equalizerBandsFlow
    }
}

class EqualizerBandsPublisherImpl(private val storageRepository: StorageRepository) :
    EqualizerBandsPublisher {
    override suspend fun setEqualizerBands(bands: ImmutableList<Short>) =
        storageRepository.storeEqualizerBands(bands)
}