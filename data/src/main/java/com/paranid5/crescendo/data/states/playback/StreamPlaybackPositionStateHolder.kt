package com.paranid5.crescendo.data.states.playback

import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.properties.storeStreamPlaybackPosition
import com.paranid5.crescendo.data.properties.streamPlaybackPositionFlow
import kotlinx.coroutines.flow.Flow

interface StreamPlaybackPositionStateSubscriber {
    val streamPlaybackPositionFlow: Flow<Long>
}

interface StreamPlaybackPositionStatePublisher {
    suspend fun setStreamPlaybackPosition(position: Long)
}

class StreamPlaybackPositionStateSubscriberImpl(private val storageRepository: StorageRepository) :
    StreamPlaybackPositionStateSubscriber {
    override val streamPlaybackPositionFlow by lazy {
        storageRepository.streamPlaybackPositionFlow
    }
}

class StreamPlaybackPositionStatePublisherImpl(private val storageRepository: StorageRepository) :
    StreamPlaybackPositionStatePublisher {
    override suspend fun setStreamPlaybackPosition(position: Long) =
        storageRepository.storeStreamPlaybackPosition(position)
}