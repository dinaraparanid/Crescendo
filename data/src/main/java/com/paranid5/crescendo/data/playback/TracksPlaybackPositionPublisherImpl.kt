package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.data.datastore.PlaybackDataStore
import com.paranid5.crescendo.domain.playback.TracksPlaybackPositionPublisher

internal class TracksPlaybackPositionPublisherImpl(
    private val playbackDataStore: PlaybackDataStore,
) : TracksPlaybackPositionPublisher {
    override suspend fun updateTracksPlaybackPosition(position: Long) =
        playbackDataStore.storeTracksPlaybackPosition(position)
}
