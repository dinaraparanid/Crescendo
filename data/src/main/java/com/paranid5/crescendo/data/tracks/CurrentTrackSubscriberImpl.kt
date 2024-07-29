package com.paranid5.crescendo.data.tracks

import com.paranid5.crescendo.data.datastore.TracksDataStore
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.tracks.CurrentTrackSubscriber
import kotlinx.coroutines.flow.combine

internal class CurrentTrackSubscriberImpl(
    tracksDataStore: TracksDataStore,
    currentPlaylistRepository: CurrentPlaylistRepository,
) : CurrentTrackSubscriber {
    override val currentTrackFlow by lazy {
        combine(
            currentPlaylistRepository.currentPlaylistFlow,
            tracksDataStore.currentTrackIndexFlow
        ) { tracks, index ->
            tracks.getOrNull(index)
        }
    }
}