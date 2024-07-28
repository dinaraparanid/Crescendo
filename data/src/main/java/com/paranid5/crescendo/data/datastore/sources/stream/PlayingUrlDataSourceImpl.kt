package com.paranid5.crescendo.data.datastore.sources.stream

import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.properties.playingUrlFlow
import com.paranid5.crescendo.data.properties.storePlayingUrl
import com.paranid5.crescendo.domain.sources.stream.PlayingUrlPublisher
import com.paranid5.crescendo.domain.sources.stream.PlayingUrlSubscriber

class PlayingUrlSubscriberImpl(private val dataStoreProvider: DataStoreProvider) :
    PlayingUrlSubscriber {
    override val playingUrlFlow by lazy {
        dataStoreProvider.playingUrlFlow
    }
}

class PlayingUrlPublisherImpl(private val dataStoreProvider: DataStoreProvider) :
    PlayingUrlPublisher {
    override suspend fun setPlayingUrl(url: String) =
        dataStoreProvider.storePlayingUrl(url)
}