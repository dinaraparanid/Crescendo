package com.paranid5.crescendo.domain.playback

interface PlayingStatePublisher {
    fun updatePlaying(isPlaying: Boolean)
}