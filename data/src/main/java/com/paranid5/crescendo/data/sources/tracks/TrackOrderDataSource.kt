package com.paranid5.crescendo.data.sources.tracks

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.storeTrackOrder
import com.paranid5.crescendo.data.properties.trackOrderFlow
import kotlinx.coroutines.flow.Flow

interface TrackOrderStateSubscriber {
    val trackOrderFlow: Flow<com.paranid5.crescendo.core.common.tracks.TrackOrder>
}

interface TrackOrderStatePublisher {
    suspend fun setTrackOrder(trackOrder: com.paranid5.crescendo.core.common.tracks.TrackOrder)
}

class TrackOrderStateSubscriberImpl(private val storageRepository: StorageRepository) :
    TrackOrderStateSubscriber {
    override val trackOrderFlow by lazy {
        storageRepository.trackOrderFlow
    }
}

class TrackOrderStatePublisherImpl(private val storageRepository: StorageRepository) :
    TrackOrderStatePublisher {
    override suspend fun setTrackOrder(trackOrder: com.paranid5.crescendo.core.common.tracks.TrackOrder) =
        storageRepository.storeTrackOrder(trackOrder)
}