package com.paranid5.crescendo.fetch_stream.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageRepository
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisherImpl
import com.paranid5.crescendo.data.states.stream.CurrentUrlStatePublisher
import com.paranid5.crescendo.data.states.stream.CurrentUrlStatePublisherImpl
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriberImpl
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