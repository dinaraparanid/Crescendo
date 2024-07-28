package com.paranid5.crescendo.data.datastore.sources.playback

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.storeTracksPlaybackPosition
import com.paranid5.crescendo.data.properties.tracksPlaybackPositionFlow
import com.paranid5.crescendo.domain.sources.playback.TracksPlaybackPositionPublisher
import com.paranid5.crescendo.domain.sources.playback.TracksPlaybackPositionSubscriber

class TracksPlaybackPositionSubscriberImpl(private val dataStoreProvider: DataStoreProvider) :
    TracksPlaybackPositionSubscriber {
    override val tracksPlaybackPositionFlow by lazy {
        dataStoreProvider.tracksPlaybackPositionFlow
    }
}

class TracksPlaybackPositionPublisherImpl(private val dataStoreProvider: DataStoreProvider) :
    TracksPlaybackPositionPublisher {
    override suspend fun setTracksPlaybackPosition(position: Long) =
        dataStoreProvider.storeTracksPlaybackPosition(position)
}