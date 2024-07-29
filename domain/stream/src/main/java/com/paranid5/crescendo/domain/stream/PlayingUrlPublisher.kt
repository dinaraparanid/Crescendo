package com.paranid5.crescendo.domain.stream

interface PlayingUrlPublisher {
    suspend fun updatePlayingUrl(url: String)
}
