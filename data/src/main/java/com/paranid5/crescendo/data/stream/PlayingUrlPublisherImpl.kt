package com.paranid5.crescendo.data.stream

import com.paranid5.crescendo.data.datastore.StreamDataStore
import com.paranid5.crescendo.domain.stream.PlayingUrlPublisher

internal class PlayingUrlPublisherImpl(
    private val streamDataStore: StreamDataStore,
) : PlayingUrlPublisher {
    override suspend fun updatePlayingUrl(url: String) =
        streamDataStore.storePlayingUrl(url)
}
