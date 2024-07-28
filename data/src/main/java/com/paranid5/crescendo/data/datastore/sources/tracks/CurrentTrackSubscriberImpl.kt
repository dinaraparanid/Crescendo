package com.paranid5.crescendo.data.datastore.sources.tracks

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.currentTrackIndexFlow
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackSubscriber
import kotlinx.coroutines.flow.combine

class CurrentTrackSubscriberImpl(
    dataStoreProvider: DataStoreProvider,
    currentPlaylistRepository: CurrentPlaylistRepository
) : CurrentTrackSubscriber {
    override val currentTrackFlow by lazy {
        combine(
            currentPlaylistRepository.tracksFlow,
            dataStoreProvider.currentTrackIndexFlow
        ) { tracks, index ->
            tracks.getOrNull(index)
        }
    }
}