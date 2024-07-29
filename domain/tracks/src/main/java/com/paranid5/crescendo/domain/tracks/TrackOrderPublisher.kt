package com.paranid5.crescendo.domain.tracks

import com.paranid5.crescendo.core.common.tracks.TrackOrder

interface TrackOrderPublisher {
    suspend fun updateTrackOrder(trackOrder: TrackOrder)
}
