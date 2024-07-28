package com.paranid5.crescendo.current_playlist.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.current_playlist.data.TrackDismissDataSource
import com.paranid5.crescendo.current_playlist.data.TrackDismissDataSourceImpl
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.datastore.sources.playback.AudioStatusPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.tracks.CurrentPlaylistPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.tracks.CurrentPlaylistSubscriberImpl
import com.paranid5.crescendo.data.datastore.sources.tracks.CurrentTrackIndexPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.tracks.CurrentTrackIndexSubscriberImpl
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentPlaylistSubscriber
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexSubscriber

class CurrentPlaylistViewModel(
    dataStoreProvider: DataStoreProvider,
    currentPlaylistRepository: CurrentPlaylistRepository,
) : ViewModel(),
    AudioStatusPublisher by AudioStatusPublisherImpl(dataStoreProvider),
    CurrentPlaylistSubscriber by CurrentPlaylistSubscriberImpl(currentPlaylistRepository),
    CurrentPlaylistPublisher by CurrentPlaylistPublisherImpl(currentPlaylistRepository),
    CurrentTrackIndexSubscriber by CurrentTrackIndexSubscriberImpl(dataStoreProvider),
    CurrentTrackIndexPublisher by CurrentTrackIndexPublisherImpl(dataStoreProvider),
    TrackDismissDataSource by TrackDismissDataSourceImpl()