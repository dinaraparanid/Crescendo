package com.paranid5.crescendo.current_playlist.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.current_playlist.data.TrackDismissDataSource
import com.paranid5.crescendo.current_playlist.data.TrackDismissDataSourceImpl
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.data.sources.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.data.sources.playback.AudioStatusStatePublisherImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistStatePublisher
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistStatePublisherImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistStateSubscriber
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistStateSubscriberImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexStatePublisher
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexStatePublisherImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexStateSubscriber
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexStateSubscriberImpl

class CurrentPlaylistViewModel(
    storageRepository: StorageRepository,
    currentPlaylistRepository: CurrentPlaylistRepository,
) : ViewModel(),
    AudioStatusStatePublisher by AudioStatusStatePublisherImpl(storageRepository),
    CurrentPlaylistStateSubscriber by CurrentPlaylistStateSubscriberImpl(currentPlaylistRepository),
    CurrentPlaylistStatePublisher by CurrentPlaylistStatePublisherImpl(currentPlaylistRepository),
    CurrentTrackIndexStateSubscriber by CurrentTrackIndexStateSubscriberImpl(storageRepository),
    CurrentTrackIndexStatePublisher by CurrentTrackIndexStatePublisherImpl(storageRepository),
    TrackDismissDataSource by TrackDismissDataSourceImpl()