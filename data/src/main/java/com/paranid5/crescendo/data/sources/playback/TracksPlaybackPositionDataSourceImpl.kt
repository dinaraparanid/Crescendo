package com.paranid5.crescendo.data.sources.playback

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.storeTracksPlaybackPosition
import com.paranid5.crescendo.data.properties.tracksPlaybackPositionFlow
import com.paranid5.crescendo.domain.sources.playback.TracksPlaybackPositionPublisher
import com.paranid5.crescendo.domain.sources.playback.TracksPlaybackPositionSubscriber

class TracksPlaybackPositionSubscriberImpl(private val storageRepository: StorageRepository) :
    TracksPlaybackPositionSubscriber {
    override val tracksPlaybackPositionFlow by lazy {
        storageRepository.tracksPlaybackPositionFlow
    }
}

class TracksPlaybackPositionPublisherImpl(private val storageRepository: StorageRepository) :
    TracksPlaybackPositionPublisher {
    override suspend fun setTracksPlaybackPosition(position: Long) =
        storageRepository.storeTracksPlaybackPosition(position)
}