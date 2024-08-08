package com.paranid5.crescendo.data.stream

import com.paranid5.crescendo.data.datastore.StreamDataStore
import com.paranid5.crescendo.domain.stream.PlayingStreamUrlPublisher

internal class PlayingStreamUrlPublisherImpl(
    private val streamDataStore: StreamDataStore,
) : PlayingStreamUrlPublisher {
    override suspend fun updatePlayingUrl(url: String) =
        streamDataStore.storePlayingUrl(url)
}
