package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.data.datastore.PlaybackDataStore
import com.paranid5.crescendo.domain.playback.TracksPlaybackPositionSubscriber

internal class TracksPlaybackPositionSubscriberImpl(
    private val playbackDataStore: PlaybackDataStore,
) : TracksPlaybackPositionSubscriber {
    override val tracksPlaybackPositionFlow by lazy {
        playbackDataStore.tracksPlaybackPositionFlow
    }
}
