package com.paranid5.crescendo.domain.sources.tracks

import com.paranid5.crescendo.core.common.tracks.TrackOrder
import kotlinx.coroutines.flow.Flow

interface TrackOrderSubscriber {
    val trackOrderFlow: Flow<TrackOrder>
}

interface TrackOrderPublisher {
    suspend fun setTrackOrder(trackOrder: TrackOrder)
}