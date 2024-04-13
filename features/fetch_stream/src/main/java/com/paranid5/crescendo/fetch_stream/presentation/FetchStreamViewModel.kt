package com.paranid5.crescendo.fetch_stream.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.AudioStatusPublisherImpl
import com.paranid5.crescendo.data.sources.stream.CurrentUrlPublisherImpl
import com.paranid5.crescendo.data.sources.stream.CurrentUrlSubscriberImpl
import com.paranid5.crescendo.domain.sources.playback.AudioStatusPublisher
import com.paranid5.crescendo.domain.sources.stream.CurrentUrlPublisher
import com.paranid5.crescendo.domain.sources.stream.CurrentUrlSubscriber
import com.paranid5.crescendo.fetch_stream.data.UrlDataSource
import com.paranid5.crescendo.fetch_stream.data.UrlDataSourceImpl

class FetchStreamViewModel(
    savedStateHandle: SavedStateHandle,
    storageRepository: StorageRepository,
) : ViewModel(),
    CurrentUrlSubscriber by CurrentUrlSubscriberImpl(storageRepository),
    CurrentUrlPublisher by CurrentUrlPublisherImpl(storageRepository),
    UrlDataSource by UrlDataSourceImpl(savedStateHandle),
    AudioStatusPublisher by AudioStatusPublisherImpl(storageRepository)