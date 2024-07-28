package com.paranid5.crescendo.fetch_stream.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.datastore.DataStoreProvider
import com.paranid5.crescendo.data.datastore.sources.playback.AudioStatusPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.stream.DownloadingUrlSubscriberImpl
import com.paranid5.crescendo.data.datastore.sources.stream.PlayingUrlPublisherImpl
import com.paranid5.crescendo.data.datastore.sources.stream.PlayingUrlSubscriberImpl
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.stream.DownloadingUrlSubscriber
import com.paranid5.crescendo.domain.sources.stream.PlayingUrlPublisher
import com.paranid5.crescendo.domain.sources.stream.PlayingUrlSubscriber
import com.paranid5.crescendo.fetch_stream.data.UrlDataSource
import com.paranid5.crescendo.fetch_stream.data.UrlDataSourceImpl

class FetchStreamViewModel(
    savedStateHandle: SavedStateHandle,
    dataStoreProvider: DataStoreProvider,
) : ViewModel(),
    PlayingUrlSubscriber by PlayingUrlSubscriberImpl(dataStoreProvider),
    PlayingUrlPublisher by PlayingUrlPublisherImpl(dataStoreProvider),
    DownloadingUrlSubscriber by DownloadingUrlSubscriberImpl(dataStoreProvider),
    UrlDataSource by UrlDataSourceImpl(savedStateHandle),
    AudioStatusPublisher by AudioStatusPublisherImpl(dataStoreProvider)