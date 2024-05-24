package com.paranid5.crescendo.data.sources.stream

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.playingUrlFlow
import com.paranid5.crescendo.data.properties.storePlayingUrl
import com.paranid5.crescendo.domain.sources.stream.PlayingUrlPublisher
import com.paranid5.crescendo.domain.sources.stream.PlayingUrlSubscriber

class PlayingUrlSubscriberImpl(private val storageRepository: StorageRepository) :
    PlayingUrlSubscriber {
    override val playingUrlFlow by lazy {
        storageRepository.playingUrlFlow
    }
}

class PlayingUrlPublisherImpl(private val storageRepository: StorageRepository) :
    PlayingUrlPublisher {
    override suspend fun setPlayingUrl(url: String) =
        storageRepository.storePlayingUrl(url)
}