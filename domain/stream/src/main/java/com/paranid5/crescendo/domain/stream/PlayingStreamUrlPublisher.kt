package com.paranid5.crescendo.domain.stream

interface PlayingStreamUrlPublisher {
    suspend fun updatePlayingUrl(url: String)
}
