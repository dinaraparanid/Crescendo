package com.paranid5.crescendo.data.tracks

import com.paranid5.crescendo.data.datastore.TracksDataStore
import com.paranid5.crescendo.domain.tracks.TrackOrderSubscriber

internal class TrackOrderSubscriberImpl(
    tracksDataStore: TracksDataStore,
) : TrackOrderSubscriber {
    override val trackOrderFlow by lazy {
        tracksDataStore.trackOrderFlow
    }
}
