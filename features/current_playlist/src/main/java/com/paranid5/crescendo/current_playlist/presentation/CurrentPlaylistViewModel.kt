package com.paranid5.crescendo.current_playlist.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.current_playlist.data.TrackDismissDataSource
import com.paranid5.crescendo.current_playlist.data.TrackDismissDataSourceImpl
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistSubscriber
import com.paranid5.crescendo.domain.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexSubscriber
import com.paranid5.crescendo.domain.tracks.TracksRepository

class CurrentPlaylistViewModel(
    currentPlaylistRepository: CurrentPlaylistRepository,
    playbackRepository: PlaybackRepository,
    tracksRepository: TracksRepository,
) : ViewModel(),
    AudioStatusPublisher by playbackRepository,
    CurrentPlaylistSubscriber by currentPlaylistRepository,
    CurrentPlaylistPublisher by currentPlaylistRepository,
    CurrentTrackIndexSubscriber by tracksRepository,
    CurrentTrackIndexPublisher by tracksRepository,
    TrackDismissDataSource by TrackDismissDataSourceImpl()
