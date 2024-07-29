package com.paranid5.crescendo.tracks.presentation

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistPublisher
import com.paranid5.crescendo.domain.current_playlist.CurrentPlaylistRepository
import com.paranid5.crescendo.domain.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.tracks.CurrentTrackIndexPublisher
import com.paranid5.crescendo.domain.tracks.CurrentTrackSubscriber
import com.paranid5.crescendo.domain.tracks.TrackOrderPublisher
import com.paranid5.crescendo.domain.tracks.TrackOrderSubscriber
import com.paranid5.crescendo.domain.tracks.TracksRepository
import com.paranid5.crescendo.tracks.data.QueryDataSource
import com.paranid5.crescendo.tracks.data.QueryDataSourceImpl
import com.paranid5.crescendo.tracks.data.SearchBarActiveDataSource
import com.paranid5.crescendo.tracks.data.SearchBarActiveDataSourceImpl
import com.paranid5.crescendo.tracks.data.TracksDataSource
import com.paranid5.crescendo.tracks.data.TracksDataSourceImpl

@Suppress("IncorrectFormatting")
class TracksViewModel(
    currentPlaylistRepository: CurrentPlaylistRepository,
    playbackRepository: PlaybackRepository,
    tracksRepository: TracksRepository,
) : ViewModel(),
    AudioStatusPublisher by playbackRepository,
    TrackOrderSubscriber by tracksRepository,
    TrackOrderPublisher by tracksRepository,
    CurrentPlaylistPublisher by currentPlaylistRepository,
    CurrentTrackIndexPublisher by tracksRepository,
    CurrentTrackSubscriber by tracksRepository,
    QueryDataSource by QueryDataSourceImpl(),
    SearchBarActiveDataSource by SearchBarActiveDataSourceImpl(),
    TracksDataSource by TracksDataSourceImpl()