package com.paranid5.crescendo.data.current_playlist

import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistSubscriber

internal class CurrentPlaylistSubscriberImpl(
    private val currentPlaylistRepository: CurrentPlaylistRepository
) : CurrentPlaylistSubscriber {
    override val currentPlaylistFlow by lazy {
        currentPlaylistRepository.currentPlaylistFlow
    }
}
