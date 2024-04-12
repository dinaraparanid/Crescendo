package com.paranid5.crescendo.fetch_stream.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.sources.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.data.sources.playback.AudioStatusStatePublisherImpl
import com.paranid5.crescendo.data.sources.stream.CurrentUrlStatePublisher
import com.paranid5.crescendo.data.sources.stream.CurrentUrlStatePublisherImpl
import com.paranid5.crescendo.data.sources.stream.CurrentUrlStateSubscriber
import com.paranid5.crescendo.data.sources.stream.CurrentUrlStateSubscriberImpl
import com.paranid5.crescendo.fetch_stream.data.UrlDataSource
import com.paranid5.crescendo.fetch_stream.data.UrlDataSourceImpl

class FetchStreamViewModel(
    savedStateHandle: SavedStateHandle,
    storageRepository: StorageRepository,
) : ViewModel(),
    CurrentUrlStateSubscriber by CurrentUrlStateSubscriberImpl(storageRepository),
    CurrentUrlStatePublisher by CurrentUrlStatePublisherImpl(storageRepository),
    UrlDataSource by UrlDataSourceImpl(savedStateHandle),
    AudioStatusStatePublisher by AudioStatusStatePublisherImpl(storageRepository)