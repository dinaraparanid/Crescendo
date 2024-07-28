package com.paranid5.crescendo.domain.playback

interface AudioSessionIdPublisher {
    fun updateAudioSessionId(audioSessionId: Int)
}