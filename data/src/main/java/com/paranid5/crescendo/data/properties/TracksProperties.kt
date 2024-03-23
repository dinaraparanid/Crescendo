package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.core.common.tracks.TrackOrder
import com.paranid5.crescendo.data.StorageRepository

inline val StorageRepository.currentTrackIndexFlow
    get() = tracksStateDataSource.currentTrackIndexFlow

suspend inline fun StorageRepository.storeCurrentTrackIndex(index: Int) =
    tracksStateDataSource.storeCurrentTrackIndex(index)

inline val StorageRepository.trackOrderFlow
    get() = tracksStateDataSource.trackOrderFlow

suspend inline fun StorageRepository.storeTrackOrder(trackOrder: TrackOrder) =
    tracksStateDataSource.storeTrackOrder(trackOrder)