package com.paranid5.crescendo.data.current_playlist

import com.paranid5.crescendo.core.common.tracks.Track
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository

class CurrentPlaylistPublisherImpl(
    private val currentPlaylistRepository: CurrentPlaylistRepository,
) : CurrentPlaylistPublisher {
    override suspend fun updateCurrentPlaylist(playlist: List<Track>) =
        currentPlaylistRepository.updateCurrentPlaylist(playlist)
}
