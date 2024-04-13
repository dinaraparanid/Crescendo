package com.paranid5.crescendo.data.sources.tracks

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.currentTrackIndexFlow
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackSubscriber
import kotlinx.coroutines.flow.combine

class CurrentTrackSubscriberImpl(
    storageRepository: StorageRepository,
    currentPlaylistRepository: CurrentPlaylistRepository
) : CurrentTrackSubscriber {
    override val currentTrackFlow by lazy {
        combine(
            currentPlaylistRepository.tracksFlow,
            storageRepository.currentTrackIndexFlow
        ) { tracks, index ->
            tracks.getOrNull(index)
        }
    }
}