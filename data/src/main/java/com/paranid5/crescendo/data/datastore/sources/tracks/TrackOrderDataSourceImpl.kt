package com.paranid5.crescendo.data.datastore.sources.tracks

import com.paranid5.crescendo.core.common.tracks.TrackOrder
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.storeTrackOrder
import com.paranid5.crescendo.data.properties.trackOrderFlow
import com.paranid5.crescendo.domain.sources.tracks.TrackOrderPublisher
import com.paranid5.crescendo.domain.sources.tracks.TrackOrderSubscriber

class TrackOrderSubscriberImpl(private val dataStoreProvider: DataStoreProvider) :
    TrackOrderSubscriber {
    override val trackOrderFlow by lazy {
        dataStoreProvider.trackOrderFlow
    }
}

class TrackOrderPublisherImpl(private val dataStoreProvider: DataStoreProvider) :
    TrackOrderPublisher {
    override suspend fun setTrackOrder(trackOrder: TrackOrder) =
        dataStoreProvider.storeTrackOrder(trackOrder)
}