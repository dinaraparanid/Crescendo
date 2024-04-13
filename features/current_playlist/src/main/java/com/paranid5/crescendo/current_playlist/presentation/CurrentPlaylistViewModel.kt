package com.paranid5.crescendo.current_playlist.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.current_playlist.data.TrackDismissDataSource
import com.paranid5.crescendo.current_playlist.data.TrackDismissDataSourceImpl
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.AudioStatusPublisherImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistPublisherImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentPlaylistSubscriberImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexPublisherImpl
import com.paranid5.crescendo.data.sources.tracks.CurrentTrackIndexSubscriberImpl
import com.paranid5.crescendo.domain.repositories.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentPlaylistSubscriber
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.domain.sources.tracks.CurrentTrackIndexSubscriber

class CurrentPlaylistViewModel(
    storageRepository: StorageRepository,
    currentPlaylistRepository: CurrentPlaylistRepository,
) : ViewModel(),
    AudioStatusPublisher by AudioStatusPublisherImpl(storageRepository),
    CurrentPlaylistSubscriber by CurrentPlaylistSubscriberImpl(currentPlaylistRepository),
    CurrentPlaylistPublisher by CurrentPlaylistPublisherImpl(currentPlaylistRepository),
    CurrentTrackIndexSubscriber by CurrentTrackIndexSubscriberImpl(storageRepository),
    CurrentTrackIndexPublisher by CurrentTrackIndexPublisherImpl(storageRepository),
    TrackDismissDataSource by TrackDismissDataSourceImpl()