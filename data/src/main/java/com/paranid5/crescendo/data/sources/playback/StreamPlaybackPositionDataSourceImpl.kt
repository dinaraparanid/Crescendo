package com.paranid5.crescendo.data.sources.playback

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.storeStreamPlaybackPosition
import com.paranid5.crescendo.data.properties.streamPlaybackPositionFlow
import com.paranid5.crescendo.domain.sources.playback.StreamPlaybackPositionPublisher
import com.paranid5.crescendo.domain.sources.playback.StreamPlaybackPositionSubscriber

class StreamPlaybackPositionSubscriberImpl(private val storageRepository: StorageRepository) :
    StreamPlaybackPositionSubscriber {
    override val streamPlaybackPositionFlow by lazy {
        storageRepository.streamPlaybackPositionFlow
    }
}

class StreamPlaybackPositionPublisherImpl(private val storageRepository: StorageRepository) :
    StreamPlaybackPositionPublisher {
    override suspend fun setStreamPlaybackPosition(position: Long) =
        storageRepository.storeStreamPlaybackPosition(position)
}