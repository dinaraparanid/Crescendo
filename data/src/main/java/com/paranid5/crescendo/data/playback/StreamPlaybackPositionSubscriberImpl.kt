package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.data.datastore.PlaybackDataStore
import com.paranid5.crescendo.domain.playback.StreamPlaybackPositionSubscriber

internal class StreamPlaybackPositionSubscriberImpl(
    playbackDataStore: PlaybackDataStore,
) : StreamPlaybackPositionSubscriber {
    override val streamPlaybackPositionFlow by lazy {
        playbackDataStore.streamPlaybackPositionFlow
    }
}
