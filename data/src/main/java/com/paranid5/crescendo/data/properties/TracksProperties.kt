package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.core.common.tracks.TrackOrder
import com.paranid5.crescendo.data.StorageHandler

inline val StorageHandler.currentTrackIndexFlow
    get() = tracksStateProvider.currentTrackIndexFlow

suspend inline fun StorageHandler.storeCurrentTrackIndex(index: Int) =
    tracksStateProvider.storeCurrentTrackIndex(index)

inline val StorageHandler.trackOrderFlow
    get() = tracksStateProvider.trackOrderFlow

suspend inline fun StorageHandler.storeTrackOrder(trackOrder: TrackOrder) =
    tracksStateProvider.storeTrackOrder(trackOrder)