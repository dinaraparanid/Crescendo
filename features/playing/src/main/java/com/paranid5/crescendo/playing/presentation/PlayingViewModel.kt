package com.paranid5.crescendo.playing.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.AudioStatusPublisherImpl
import com.paranid5.crescendo.data.sources.playback.AudioStatusSubscriberImpl
import com.paranid5.crescendo.data.sources.playback.RepeatingSubscriberImpl
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionPublisherImpl
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionSubscriberImpl
import com.paranid5.crescendo.data.sources.playback.TracksPlaybackPositionPublisherImpl
import com.paranid5.crescendo.data.sources.playback.TracksPlaybackPositionSubscriberImpl
import com.paranid5.crescendo.data.sources.stream.CurrentMetadataSubscriberImpl
import com.paranid5.crescendo.data.sources.stream.PlayingUrlSubscriberImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackSubscriberImpl
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.playback.AudioStatusSubscriber
import com.paranid5.crescendo.domain.sources.playback.RepeatingSubscriber
import com.paranid5.crescendo.domain.sources.playback.StreamPlaybackPositionPublisher
import com.paranid5.crescendo.domain.sources.playback.StreamPlaybackPositionSubscriber
import com.paranid5.crescendo.domain.sources.playback.TracksPlaybackPositionPublisher
import com.paranid5.crescendo.domain.sources.playback.TracksPlaybackPositionSubscriber
import com.paranid5.crescendo.domain.sources.stream.CurrentMetadataSubscriber
import com.paranid5.crescendo.domain.sources.stream.PlayingUrlSubscriber
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackSubscriber

@Suppress("IncorrectFormatting")
class PlayingViewModel(
    storageRepository: StorageRepository,
    currentPlaylistRepository: CurrentPlaylistRepository,
) : ViewModel(),
    AudioStatusSubscriber by AudioStatusSubscriberImpl(storageRepository),
    AudioStatusPublisher by AudioStatusPublisherImpl(storageRepository),
    StreamPlaybackPositionSubscriber by StreamPlaybackPositionSubscriberImpl(storageRepository),
    StreamPlaybackPositionPublisher by StreamPlaybackPositionPublisherImpl(storageRepository),
    TracksPlaybackPositionSubscriber by TracksPlaybackPositionSubscriberImpl(storageRepository),
    TracksPlaybackPositionPublisher by TracksPlaybackPositionPublisherImpl(storageRepository),
    RepeatingSubscriber by RepeatingSubscriberImpl(storageRepository),
    CurrentTrackSubscriber by CurrentTrackSubscriberImpl(storageRepository, currentPlaylistRepository),
    CurrentMetadataSubscriber by CurrentMetadataSubscriberImpl(storageRepository),
    PlayingUrlSubscriber by PlayingUrlSubscriberImpl(storageRepository)
