package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.data.datastore.PlaybackDataStore
import com.paranid5.crescendo.domain.playback.AudioStatusSubscriber

internal class AudioStatusSubscriberImpl(
    playbackDataStore: PlaybackDataStore
) : AudioStatusSubscriber {
    override val playbackStatusFlow by lazy {
        playbackDataStore.playbackStatusFlow
    }
}
