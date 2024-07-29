package com.paranid5.crescendo.data.tracks

import com.paranid5.crescendo.data.datastore.TracksDataStore
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexSubscriber

internal class CurrentTrackIndexSubscriberImpl(
    tracksDataStore: TracksDataStore,
) : CurrentTrackIndexSubscriber {
    override val currentTrackIndexFlow by lazy {
        tracksDataStore.currentTrackIndexFlow
    }
}
