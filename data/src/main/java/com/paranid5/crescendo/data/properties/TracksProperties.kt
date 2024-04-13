package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.core.common.tracks.TrackOrder
import com.paranid5.crescendo.data.StorageRepository

val StorageRepository.currentTrackIndexFlow
    get() = tracksStateDataSource.currentTrackIndexFlow

suspend fun StorageRepository.storeCurrentTrackIndex(index: Int) =
    tracksStateDataSource.storeCurrentTrackIndex(index)

val StorageRepository.trackOrderFlow
    get() = tracksStateDataSource.trackOrderFlow

suspend fun StorageRepository.storeTrackOrder(trackOrder: TrackOrder) =
    tracksStateDataSource.storeTrackOrder(trackOrder)