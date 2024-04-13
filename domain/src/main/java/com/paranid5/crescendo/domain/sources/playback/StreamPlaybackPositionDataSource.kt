package com.paranid5.crescendo.domain.sources.playback

import kotlinx.coroutines.flow.Flow

interface StreamPlaybackPositionSubscriber {
    val streamPlaybackPositionFlow: Flow<Long>
}

interface StreamPlaybackPositionPublisher {
    suspend fun setStreamPlaybackPosition(position: Long)
}