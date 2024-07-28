package com.paranid5.crescendo.domain.playback

interface PlaybackRepository :
    PlayingStateSubscriber,
    PlayingStatePublisher,
    AudioSessionIdSubscriber,
    AudioSessionIdPublisher {
    companion object {
        const val UNDEFINED_AUDIO_SESSION_ID = 0
    }
}
