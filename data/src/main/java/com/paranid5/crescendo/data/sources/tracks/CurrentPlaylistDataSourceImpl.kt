package com.paranid5.crescendo.data.sources.tracks

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.sources.tracks.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentPlaylistSubscriber

class CurrentPlaylistSubscriberImpl(
    private val currentPlaylistRepository: CurrentPlaylistRepository
) : CurrentPlaylistSubscriber {
    override val currentPlaylistFlow by lazy {
        currentPlaylistRepository.tracksFlow
    }
}

class CurrentPlaylistPublisherImpl(
    private val currentPlaylistRepository: CurrentPlaylistRepository
) : CurrentPlaylistPublisher {
    override suspend fun setCurrentPlaylist(playlist: List<Track>) =
        currentPlaylistRepository.replacePlaylistAsync(playlist).join()
}