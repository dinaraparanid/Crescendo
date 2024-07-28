package com.paranid5.crescendo.data.datastore.sources.playback

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.storeStreamPlaybackPosition
import com.paranid5.crescendo.data.properties.streamPlaybackPositionFlow
import com.paranid5.crescendo.domain.sources.playback.StreamPlaybackPositionPublisher
import com.paranid5.crescendo.domain.sources.playback.StreamPlaybackPositionSubscriber

class StreamPlaybackPositionSubscriberImpl(private val dataStoreProvider: DataStoreProvider) :
    StreamPlaybackPositionSubscriber {
    override val streamPlaybackPositionFlow by lazy {
        dataStoreProvider.streamPlaybackPositionFlow
    }
}

class StreamPlaybackPositionPublisherImpl(private val dataStoreProvider: DataStoreProvider) :
    StreamPlaybackPositionPublisher {
    override suspend fun setStreamPlaybackPosition(position: Long) =
        dataStoreProvider.storeStreamPlaybackPosition(position)
}