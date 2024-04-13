package com.paranid5.crescendo.data.sources.tracks

import com.paranid5.crescendo.core.common.tracks.TrackOrder
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.storeTrackOrder
import com.paranid5.crescendo.data.properties.trackOrderFlow
import com.paranid5.crescendo.domain.sources.tracks.TrackOrderPublisher
import com.paranid5.crescendo.domain.sources.tracks.TrackOrderSubscriber

class TrackOrderSubscriberImpl(private val storageRepository: StorageRepository) :
    TrackOrderSubscriber {
    override val trackOrderFlow by lazy {
        storageRepository.trackOrderFlow
    }
}

class TrackOrderPublisherImpl(private val storageRepository: StorageRepository) :
    TrackOrderPublisher {
    override suspend fun setTrackOrder(trackOrder: TrackOrder) =
        storageRepository.storeTrackOrder(trackOrder)
}