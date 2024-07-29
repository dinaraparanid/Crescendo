package com.paranid5.crescendo.domain.playback

interface TracksPlaybackPositionPublisher {
    suspend fun updateTracksPlaybackPosition(position: Long)
}
