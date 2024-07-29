package com.paranid5.crescendo.domain.playback

interface StreamPlaybackPositionPublisher {
    suspend fun updateStreamPlaybackPosition(position: Long)
}
