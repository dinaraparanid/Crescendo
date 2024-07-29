package com.paranid5.crescendo.domain.current_playlist

import com.paranid5.crescendo.core.common.tracks.Track

interface CurrentPlaylistPublisher {
    suspend fun updateCurrentPlaylist(playlist: List<Track>)
}
