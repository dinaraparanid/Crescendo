package com.paranid5.crescendo.fetch_stream.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.AudioStatusPublisherImpl
import com.paranid5.crescendo.data.sources.stream.DownloadingUrlSubscriberImpl
import com.paranid5.crescendo.data.sources.stream.PlayingUrlPublisherImpl
import com.paranid5.crescendo.data.sources.stream.PlayingUrlSubscriberImpl
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.stream.DownloadingUrlSubscriber
import com.paranid5.crescendo.domain.sources.stream.PlayingUrlPublisher
import com.paranid5.crescendo.domain.sources.stream.PlayingUrlSubscriber
import com.paranid5.crescendo.fetch_stream.data.UrlDataSource
import com.paranid5.crescendo.fetch_stream.data.UrlDataSourceImpl

class FetchStreamViewModel(
    savedStateHandle: SavedStateHandle,
    storageRepository: StorageRepository,
) : ViewModel(),
    PlayingUrlSubscriber by PlayingUrlSubscriberImpl(storageRepository),
    PlayingUrlPublisher by PlayingUrlPublisherImpl(storageRepository),
    DownloadingUrlSubscriber by DownloadingUrlSubscriberImpl(storageRepository),
    UrlDataSource by UrlDataSourceImpl(savedStateHandle),
    AudioStatusPublisher by AudioStatusPublisherImpl(storageRepository)