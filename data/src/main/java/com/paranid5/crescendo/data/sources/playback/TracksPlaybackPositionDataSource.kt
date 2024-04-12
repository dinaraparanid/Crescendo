package com.paranid5.crescendo.data.sources.playback

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.storeTracksPlaybackPosition
import com.paranid5.crescendo.data.properties.tracksPlaybackPositionFlow
import kotlinx.coroutines.flow.Flow

interface TracksPlaybackPositionStateSubscriber {
    val tracksPlaybackPositionFlow: Flow<Long>
}

interface TracksPlaybackPositionStatePublisher {
    suspend fun setTracksPlaybackPosition(position: Long)
}

class TracksPlaybackPositionStateSubscriberImpl(private val storageRepository: StorageRepository) :
    TracksPlaybackPositionStateSubscriber {
    override val tracksPlaybackPositionFlow by lazy {
        storageRepository.tracksPlaybackPositionFlow
    }
}

class TracksPlaybackPositionStatePublisherImpl(private val storageRepository: StorageRepository) :
    TracksPlaybackPositionStatePublisher {
    override suspend fun setTracksPlaybackPosition(position: Long) =
        storageRepository.storeTracksPlaybackPosition(position)
}