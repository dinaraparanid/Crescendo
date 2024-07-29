package com.paranid5.crescendo.data.tracks

import com.paranid5.crescendo.core.common.tracks.TrackOrder
import com.paranid5.crescendo.data.datastore.TracksDataStore
import com.paranid5.crescendo.domain.tracks.TrackOrderPublisher

internal class TrackOrderPublisherImpl(
    private val tracksDataStore: TracksDataStore,
) : TrackOrderPublisher {
    override suspend fun updateTrackOrder(trackOrder: TrackOrder) =
        tracksDataStore.storeTrackOrder(trackOrder)
}
