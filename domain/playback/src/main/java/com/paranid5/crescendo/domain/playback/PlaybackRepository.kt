package com.paranid5.crescendo.domain.playback

interface PlaybackRepository :
    PlayingStateSubscriber,
    PlayingStatePublisher,
    StreamPlaybackPositionSubscriber,
    StreamPlaybackPositionPublisher,
    TracksPlaybackPositionSubscriber,
    TracksPlaybackPositionPublisher,
    AudioStatusSubscriber,
    AudioStatusPublisher,
    RepeatingSubscriber,
    RepeatingPublisher,
    AudioSessionIdSubscriber,
    AudioSessionIdPublisher {
    companion object {
        const val UNDEFINED_AUDIO_SESSION_ID = 0
    }
}
