package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.data.datastore.PlaybackDataStore
import com.paranid5.crescendo.domain.playback.StreamPlaybackPositionPublisher

internal class StreamPlaybackPositionPublisherImpl(
    private val playbackDataStore: PlaybackDataStore,
) : StreamPlaybackPositionPublisher {
    override suspend fun updateStreamPlaybackPosition(position: Long) =
        playbackDataStore.storeStreamPlaybackPosition(position)
}
