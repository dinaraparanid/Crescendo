package com.paranid5.crescendo.presentation.main.playing

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.data.sources.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.data.sources.playback.AudioStatusStatePublisherImpl
import com.paranid5.crescendo.data.sources.playback.AudioStatusStateSubscriber
import com.paranid5.crescendo.data.sources.playback.AudioStatusStateSubscriberImpl
import com.paranid5.crescendo.data.sources.playback.RepeatingStateSubscriber
import com.paranid5.crescendo.data.sources.playback.RepeatingStateSubscriberImpl
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionStatePublisher
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionStatePublisherImpl
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionStateSubscriber
import com.paranid5.crescendo.data.sources.playback.StreamPlaybackPositionStateSubscriberImpl
import com.paranid5.crescendo.data.sources.playback.TracksPlaybackPositionStatePublisher
import com.paranid5.crescendo.data.sources.playback.TracksPlaybackPositionStatePublisherImpl
import com.paranid5.crescendo.data.sources.playback.TracksPlaybackPositionStateSubscriber
import com.paranid5.crescendo.data.sources.playback.TracksPlaybackPositionStateSubscriberImpl
import com.paranid5.crescendo.data.sources.stream.CurrentUrlStateSubscriber
import com.paranid5.crescendo.data.sources.stream.CurrentUrlStateSubscriberImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackStateSubscriber
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackStateSubscriberImpl
import com.paranid5.crescendo.presentation.main.playing.states.CacheDialogStateHolder
import com.paranid5.crescendo.presentation.main.playing.states.CacheDialogStateHolderImpl

@Suppress("IncorrectFormatting")
class PlayingViewModel(
    storageRepository: StorageRepository,
    currentPlaylistRepository: CurrentPlaylistRepository,
) : ViewModel(),
    AudioStatusStateSubscriber by AudioStatusStateSubscriberImpl(storageRepository),
    AudioStatusStatePublisher by AudioStatusStatePublisherImpl(storageRepository),
    StreamPlaybackPositionStateSubscriber by StreamPlaybackPositionStateSubscriberImpl(storageRepository),
    StreamPlaybackPositionStatePublisher by StreamPlaybackPositionStatePublisherImpl(storageRepository),
    TracksPlaybackPositionStateSubscriber by TracksPlaybackPositionStateSubscriberImpl(storageRepository),
    TracksPlaybackPositionStatePublisher by TracksPlaybackPositionStatePublisherImpl(storageRepository),
    RepeatingStateSubscriber by RepeatingStateSubscriberImpl(storageRepository),
    CurrentTrackStateSubscriber by CurrentTrackStateSubscriberImpl(storageRepository, currentPlaylistRepository),
    CurrentUrlStateSubscriber by CurrentUrlStateSubscriberImpl(storageRepository),
    CacheDialogStateHolder by CacheDialogStateHolderImpl(storageRepository)
