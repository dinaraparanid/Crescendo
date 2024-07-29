package com.paranid5.crescendo.data.playback

import com.paranid5.crescendo.domain.playback.AudioSessionIdPublisher
import com.paranid5.crescendo.domain.playback.AudioSessionIdSubscriber
import com.paranid5.crescendo.domain.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.playback.AudioStatusSubscriber
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.playback.PlayingStatePublisher
import com.paranid5.crescendo.domain.playback.PlayingStateSubscriber
import com.paranid5.crescendo.domain.playback.RepeatingPublisher
import com.paranid5.crescendo.domain.playback.RepeatingSubscriber
import com.paranid5.crescendo.domain.playback.StreamPlaybackPositionPublisher
import com.paranid5.crescendo.domain.playback.StreamPlaybackPositionSubscriber
import com.paranid5.crescendo.domain.playback.TracksPlaybackPositionPublisher
import com.paranid5.crescendo.domain.playback.TracksPlaybackPositionSubscriber

internal class PlaybackRepositoryImpl(
    playingStateSubscriber: PlayingStateSubscriber,
    playingStatePublisher: PlayingStatePublisher,
    streamPlaybackPositionSubscriber: StreamPlaybackPositionSubscriber,
    streamPlaybackPositionPublisher: StreamPlaybackPositionPublisher,
    tracksPlaybackPositionSubscriber: TracksPlaybackPositionSubscriber,
    tracksPlaybackPositionPublisher: TracksPlaybackPositionPublisher,
    audioStatusSubscriber: AudioStatusSubscriber,
    audioStatusPublisher: AudioStatusPublisher,
    repeatingSubscriber: RepeatingSubscriber,
    repeatingPublisher: RepeatingPublisher,
    audioSessionIdSubscriber: AudioSessionIdSubscriber,
    audioSessionIdPublisher: AudioSessionIdPublisher,
) : PlaybackRepository,
    PlayingStateSubscriber by playingStateSubscriber,
    PlayingStatePublisher by playingStatePublisher,
    StreamPlaybackPositionSubscriber by streamPlaybackPositionSubscriber,
    StreamPlaybackPositionPublisher by streamPlaybackPositionPublisher,
    TracksPlaybackPositionSubscriber by tracksPlaybackPositionSubscriber,
    TracksPlaybackPositionPublisher by tracksPlaybackPositionPublisher,
    AudioStatusSubscriber by audioStatusSubscriber,
    AudioStatusPublisher by audioStatusPublisher,
    RepeatingSubscriber by repeatingSubscriber,
    RepeatingPublisher by repeatingPublisher,
    AudioSessionIdSubscriber by audioSessionIdSubscriber,
    AudioSessionIdPublisher by audioSessionIdPublisher
