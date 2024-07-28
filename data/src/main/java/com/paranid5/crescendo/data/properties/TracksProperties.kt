package com.paranid5.crescendo.data.properties

import com.paranid5.crescendo.core.common.tracks.TrackOrder
import com.paranid5.crescendo.data.datastore.DataStoreProvider

val DataStoreProvider.currentTrackIndexFlow
    get() = tracksStateDataSource.currentTrackIndexFlow

suspend fun DataStoreProvider.storeCurrentTrackIndex(index: Int) =
    tracksStateDataSource.storeCurrentTrackIndex(index)

val DataStoreProvider.trackOrderFlow
    get() = tracksStateDataSource.trackOrderFlow

suspend fun DataStoreProvider.storeTrackOrder(trackOrder: TrackOrder) =
    tracksStateDataSource.storeTrackOrder(trackOrder)