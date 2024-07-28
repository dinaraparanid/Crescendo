package com.paranid5.crescendo.playing.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.datastore.sources.playback.AudioStatusPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.playback.AudioStatusSubscriberImpl
import com.paranid5.crescendo.data.datastore.sources.playback.RepeatingSubscriberImpl
import com.paranid5.crescendo.data.datastore.sources.playback.StreamPlaybackPositionPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.playback.StreamPlaybackPositionSubscriberImpl
import com.paranid5.crescendo.data.datastore.sources.playback.TracksPlaybackPositionPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.playback.TracksPlaybackPositionSubscriberImpl
import com.paranid5.crescendo.data.datastore.sources.stream.CurrentMetadataSubscriberImpl
import com.paranid5.crescendo.data.datastore.sources.stream.PlayingUrlSubscriberImpl
import com.paranid5.crescendo.data.datastore.sources.tracks.CurrentTrackSubscriberImpl
import com.paranid5.crescendo.domain.playback.AudioSessionIdSubscriber
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.playback.PlayingStateSubscriber
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
    dataStoreProvider: DataStoreProvider,
    currentPlaylistRepository: CurrentPlaylistRepository,
    playbackRepository: PlaybackRepository
) : ViewModel(),
    AudioSessionIdSubscriber by playbackRepository,
    PlayingStateSubscriber by playbackRepository,
    AudioStatusSubscriber by AudioStatusSubscriberImpl(dataStoreProvider),
    AudioStatusPublisher by AudioStatusPublisherImpl(dataStoreProvider),
    StreamPlaybackPositionSubscriber by StreamPlaybackPositionSubscriberImpl(dataStoreProvider),
    StreamPlaybackPositionPublisher by StreamPlaybackPositionPublisherImpl(dataStoreProvider),
    TracksPlaybackPositionSubscriber by TracksPlaybackPositionSubscriberImpl(dataStoreProvider),
    TracksPlaybackPositionPublisher by TracksPlaybackPositionPublisherImpl(dataStoreProvider),
    RepeatingSubscriber by RepeatingSubscriberImpl(dataStoreProvider),
    CurrentTrackSubscriber by CurrentTrackSubscriberImpl(dataStoreProvider, currentPlaylistRepository),
    CurrentMetadataSubscriber by CurrentMetadataSubscriberImpl(dataStoreProvider),
    PlayingUrlSubscriber by PlayingUrlSubscriberImpl(dataStoreProvider)
