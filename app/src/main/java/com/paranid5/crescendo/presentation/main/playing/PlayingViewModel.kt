package com.paranid5.crescendo.presentation.main.playing

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.AudioStatusStateSubscriber
import com.paranid5.crescendo.data.states.playback.AudioStatusStateSubscriberImpl
import com.paranid5.crescendo.data.states.playback.RepeatingStateSubscriber
import com.paranid5.crescendo.data.states.playback.RepeatingStateSubscriberImpl
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStatePublisher
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStateSubscriber
import com.paranid5.crescendo.data.states.playback.StreamPlaybackPositionStateSubscriberImpl
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStatePublisher
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStatePublisherImpl
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStateSubscriber
import com.paranid5.crescendo.data.states.playback.TracksPlaybackPositionStateSubscriberImpl
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriberImpl
import com.paranid5.crescendo.data.states.tracks.CurrentTrackStateSubscriber
import com.paranid5.crescendo.data.states.tracks.CurrentTrackStateSubscriberImpl
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
