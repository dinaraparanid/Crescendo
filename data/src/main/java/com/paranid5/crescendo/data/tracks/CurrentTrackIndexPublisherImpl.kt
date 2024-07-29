package com.paranid5.crescendo.data.tracks

import com.paranid5.crescendo.data.datastore.TracksDataStore
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexPublisher

internal class CurrentTrackIndexPublisherImpl(
    private val tracksDataStore: TracksDataStore,
) : CurrentTrackIndexPublisher {
    override suspend fun updateCurrentTrackIndex(index: Int) =
        tracksDataStore.storeCurrentTrackIndex(index)
}
