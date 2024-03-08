package com.paranid5.crescendo.data.states.tracks

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.storeTrackOrder
import com.paranid5.crescendo.data.properties.trackOrderFlow
import com.paranid5.crescendo.core.common.tracks.TrackOrder
import kotlinx.coroutines.flow.Flow

interface TrackOrderStateSubscriber {
    val trackOrderFlow: Flow<com.paranid5.crescendo.core.common.tracks.TrackOrder>
}

interface TrackOrderStatePublisher {
    suspend fun setTrackOrder(trackOrder: com.paranid5.crescendo.core.common.tracks.TrackOrder)
}

class TrackOrderStateSubscriberImpl(private val storageHandler: StorageHandler) :
    TrackOrderStateSubscriber {
    override val trackOrderFlow by lazy {
        storageHandler.trackOrderFlow
    }
}

class TrackOrderStatePublisherImpl(private val storageHandler: StorageHandler) :
    TrackOrderStatePublisher {
    override suspend fun setTrackOrder(trackOrder: com.paranid5.crescendo.core.common.tracks.TrackOrder) =
        storageHandler.storeTrackOrder(trackOrder)
}