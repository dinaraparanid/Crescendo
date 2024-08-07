package com.paranid5.crescendo.fetch_stream.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.domain.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.playback.PlaybackRepository
import com.paranid5.crescendo.domain.stream.DownloadingUrlSubscriber
import com.paranid5.crescendo.domain.stream.PlayingStreamUrlPublisher
import com.paranid5.crescendo.domain.stream.PlayingStreamUrlSubscriber
import com.paranid5.crescendo.domain.stream.StreamRepository
import com.paranid5.crescendo.fetch_stream.data.UrlDataSource
import com.paranid5.crescendo.fetch_stream.data.UrlDataSourceImpl

class FetchStreamViewModel(
    savedStateHandle: SavedStateHandle,
    streamRepository: StreamRepository,
    playbackRepository: PlaybackRepository,
) : ViewModel(),
    PlayingStreamUrlSubscriber by streamRepository,
    PlayingStreamUrlPublisher by streamRepository,
    DownloadingUrlSubscriber by streamRepository,
    AudioStatusPublisher by playbackRepository,
    UrlDataSource by UrlDataSourceImpl(savedStateHandle)