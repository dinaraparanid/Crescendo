package com.paranid5.crescendo.tracks.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.data.sources.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.data.sources.playback.AudioStatusStatePublisherImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistStatePublisher
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistStatePublisherImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexStatePublisher
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexStatePublisherImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackStateSubscriber
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackStateSubscriberImpl
import com.paranid5.crescendo.data.sources.tracks.TrackOrderStatePublisher
import com.paranid5.crescendo.data.sources.tracks.TrackOrderStatePublisherImpl
import com.paranid5.crescendo.data.sources.tracks.TrackOrderStateSubscriber
import com.paranid5.crescendo.data.sources.tracks.TrackOrderStateSubscriberImpl
import com.paranid5.crescendo.tracks.data.QueryDataSource
import com.paranid5.crescendo.tracks.data.QueryDataSourceImpl
import com.paranid5.crescendo.tracks.data.SearchBarActiveDataSource
import com.paranid5.crescendo.tracks.data.SearchBarActiveDataSourceImpl
import com.paranid5.crescendo.tracks.data.TracksDataSource
import com.paranid5.crescendo.tracks.data.TracksDataSourceImpl

@Suppress("IncorrectFormatting")
class TracksViewModel(
    storageRepository: StorageRepository,
    currentPlaylistRepository: CurrentPlaylistRepository
) : ViewModel(),
    AudioStatusStatePublisher by AudioStatusStatePublisherImpl(storageRepository),
    TrackOrderStateSubscriber by TrackOrderStateSubscriberImpl(storageRepository),
    TrackOrderStatePublisher by TrackOrderStatePublisherImpl(storageRepository),
    CurrentPlaylistStatePublisher by CurrentPlaylistStatePublisherImpl(currentPlaylistRepository),
    CurrentTrackIndexStatePublisher by CurrentTrackIndexStatePublisherImpl(storageRepository),
    CurrentTrackStateSubscriber by CurrentTrackStateSubscriberImpl(storageRepository, currentPlaylistRepository),
    QueryDataSource by QueryDataSourceImpl(),
    SearchBarActiveDataSource by SearchBarActiveDataSourceImpl(),
    TracksDataSource by TracksDataSourceImpl()