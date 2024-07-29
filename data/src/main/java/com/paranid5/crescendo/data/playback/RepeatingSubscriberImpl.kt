package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.data.datastore.PlaybackDataStore
import com.paranid5.crescendo.domain.playback.RepeatingSubscriber

internal class RepeatingSubscriberImpl(
    playbackDataStore: PlaybackDataStore,
) : RepeatingSubscriber {
    override val isRepeatingFlow by lazy {
        playbackDataStore.isRepeatingFlow
    }
}
