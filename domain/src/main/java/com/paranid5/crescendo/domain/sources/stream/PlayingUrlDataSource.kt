package com.paranid5.crescendo.domain.sources.stream

import kotlinx.coroutines.flow.Flow

interface PlayingUrlSubscriber {
    val playingUrlFlow: Flow<String>
}

interface PlayingUrlPublisher {
    suspend fun setPlayingUrl(url: String)
}