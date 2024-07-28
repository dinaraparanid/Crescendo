package com.paranid5.crescendo.tracks.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.datastore.sources.playback.AudioStatusPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.tracks.CurrentPlaylistPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.tracks.CurrentTrackIndexPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.tracks.CurrentTrackSubscriberImpl
import com.paranid5.crescendo.data.datastore.sources.tracks.TrackOrderPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.tracks.TrackOrderSubscriberImpl
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackSubscriber
import com.paranid5.crescendo.domain.sources.tracks.TrackOrderPublisher
import com.paranid5.crescendo.domain.sources.tracks.TrackOrderSubscriber
import com.paranid5.crescendo.tracks.data.QueryDataSource
import com.paranid5.crescendo.tracks.data.QueryDataSourceImpl
import com.paranid5.crescendo.tracks.data.SearchBarActiveDataSource
import com.paranid5.crescendo.tracks.data.SearchBarActiveDataSourceImpl
import com.paranid5.crescendo.tracks.data.TracksDataSource
import com.paranid5.crescendo.tracks.data.TracksDataSourceImpl

@Suppress("IncorrectFormatting")
class TracksViewModel(
    dataStoreProvider: DataStoreProvider,
    currentPlaylistRepository: CurrentPlaylistRepository
) : ViewModel(),
    AudioStatusPublisher by AudioStatusPublisherImpl(dataStoreProvider),
    TrackOrderSubscriber by TrackOrderSubscriberImpl(dataStoreProvider),
    TrackOrderPublisher by TrackOrderPublisherImpl(dataStoreProvider),
    CurrentPlaylistPublisher by CurrentPlaylistPublisherImpl(currentPlaylistRepository),
    CurrentTrackIndexPublisher by CurrentTrackIndexPublisherImpl(dataStoreProvider),
    CurrentTrackSubscriber by CurrentTrackSubscriberImpl(dataStoreProvider, currentPlaylistRepository),
    QueryDataSource by QueryDataSourceImpl(),
    SearchBarActiveDataSource by SearchBarActiveDataSourceImpl(),
    TracksDataSource by TracksDataSourceImpl()