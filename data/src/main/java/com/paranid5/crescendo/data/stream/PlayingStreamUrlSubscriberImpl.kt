package com.paranid5.crescendo.data.stream

import com.paranid5.crescendo.data.datastore.StreamDataStore
import com.paranid5.crescendo.domain.stream.PlayingStreamUrlSubscriber

internal class PlayingStreamUrlSubscriberImpl(
    streamDataStore: StreamDataStore,
) : PlayingStreamUrlSubscriber {
    override val playingUrlFlow by lazy {
        streamDataStore.playingUrlFlow
    }
}
