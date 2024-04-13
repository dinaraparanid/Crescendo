package com.paranid5.crescendo.tracks.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.AudioStatusPublisherImpl
import com.paranid5.crescendo.data.sources.tracks.*
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.tracks.*
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
    AudioStatusPublisher by AudioStatusPublisherImpl(storageRepository),
    TrackOrderSubscriber by TrackOrderSubscriberImpl(storageRepository),
    TrackOrderPublisher by TrackOrderPublisherImpl(storageRepository),
    CurrentPlaylistPublisher by CurrentPlaylistPublisherImpl(currentPlaylistRepository),
    CurrentTrackIndexPublisher by CurrentTrackIndexPublisherImpl(storageRepository),
    CurrentTrackSubscriber by CurrentTrackSubscriberImpl(storageRepository, currentPlaylistRepository),
    QueryDataSource by QueryDataSourceImpl(),
    SearchBarActiveDataSource by SearchBarActiveDataSourceImpl(),
    TracksDataSource by TracksDataSourceImpl()