package com.paranid5.crescendo.domain.sources.playback

import kotlinx.coroutines.flow.Flow

interface TracksPlaybackPositionSubscriber {
    val tracksPlaybackPositionFlow: Flow<Long>
}

interface TracksPlaybackPositionPublisher {
    suspend fun setTracksPlaybackPosition(position: Long)
}