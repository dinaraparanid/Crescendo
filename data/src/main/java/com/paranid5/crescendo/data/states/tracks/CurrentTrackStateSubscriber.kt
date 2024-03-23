package com.paranid5.crescendo.data.states.tracks

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.data.properties.currentTrackIndexFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface CurrentTrackStateSubscriber {
    val currentTrackFlow: Flow<com.paranid5.crescendo.core.common.tracks.Track?>
}

class CurrentTrackStateSubscriberImpl(
    storageRepository: StorageRepository,
    currentPlaylistRepository: CurrentPlaylistRepository
) : CurrentTrackStateSubscriber {
    override val currentTrackFlow by lazy {
        combine(
            currentPlaylistRepository.tracksFlow,
            storageRepository.currentTrackIndexFlow
        ) { tracks, index ->
            tracks.getOrNull(index)
        }
    }
}