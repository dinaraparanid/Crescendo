package com.paranid5.crescendo.domain.current_playlist

import com.paranid5.crescendo.core.common.tracks.Track

interface CurrentPlaylistRepository : CurrentPlaylistSubscriber, CurrentPlaylistPublisher {
    suspend fun addTrackToPlaylist(track: Track)
}
