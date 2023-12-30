package com.paranid5.crescendo.data.states.playback

import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.properties.storeTracksPlaybackPosition
import com.paranid5.crescendo.data.properties.tracksPlaybackPositionFlow
import kotlinx.coroutines.flow.Flow

interface TracksPlaybackPositionStateSubscriber {
    val tracksPlaybackPositionFlow: Flow<Long>
}

interface TracksPlaybackPositionStatePublisher {
    suspend fun setTracksPlaybackPosition(position: Long)
}

class TracksPlaybackPositionStateSubscriberImpl(private val storageHandler: StorageHandler) :
    TracksPlaybackPositionStateSubscriber {
    override val tracksPlaybackPositionFlow by lazy {
        storageHandler.tracksPlaybackPositionFlow
    }
}

class TracksPlaybackPositionStatePublisherImpl(private val storageHandler: StorageHandler) :
    TracksPlaybackPositionStatePublisher {
    override suspend fun setTracksPlaybackPosition(position: Long) =
        storageHandler.storeTracksPlaybackPosition(position)
}