package com.paranid5.crescendo.data.stream

import com.paranid5.crescendo.data.datastore.StreamDataStore
import com.paranid5.crescendo.domain.stream.PlayingUrlSubscriber

internal class PlayingUrlSubscriberImpl(
    streamDataStore: StreamDataStore,
) : PlayingUrlSubscriber {
    override val playingUrlFlow by lazy {
        streamDataStore.playingUrlFlow
    }
}
