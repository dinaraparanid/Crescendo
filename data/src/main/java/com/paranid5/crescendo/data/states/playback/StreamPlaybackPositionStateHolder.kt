package com.paranid5.crescendo.data.states.playback

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.storeStreamPlaybackPosition
import com.paranid5.crescendo.data.properties.streamPlaybackPositionFlow
import kotlinx.coroutines.flow.Flow

interface StreamPlaybackPositionStateSubscriber {
    val streamPlaybackPositionFlow: Flow<Long>
}

interface StreamPlaybackPositionStatePublisher {
    suspend fun setStreamPlaybackPosition(position: Long)
}

class StreamPlaybackPositionStateSubscriberImpl(private val storageHandler: StorageHandler) :
    StreamPlaybackPositionStateSubscriber {
    override val streamPlaybackPositionFlow by lazy {
        storageHandler.streamPlaybackPositionFlow
    }
}

class StreamPlaybackPositionStatePublisherImpl(private val storageHandler: StorageHandler) :
    StreamPlaybackPositionStatePublisher {
    override suspend fun setStreamPlaybackPosition(position: Long) =
        storageHandler.storeStreamPlaybackPosition(position)
}