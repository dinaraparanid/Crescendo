package com.paranid5.crescendo.presentation.main.fetch_stream

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisher
import com.paranid5.crescendo.data.states.playback.AudioStatusStatePublisherImpl
import com.paranid5.crescendo.data.states.stream.CurrentUrlStatePublisher
import com.paranid5.crescendo.data.states.stream.CurrentUrlStatePublisherImpl
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriber
import com.paranid5.crescendo.data.states.stream.CurrentUrlStateSubscriberImpl
import com.paranid5.crescendo.presentation.main.fetch_stream.states.UrlStateHolder
import com.paranid5.crescendo.presentation.main.fetch_stream.states.UrlStateHolderImpl

class FetchStreamViewModel(
    savedStateHandle: SavedStateHandle,
    storageHandler: StorageHandler,
) : ViewModel(),
    CurrentUrlStateSubscriber by CurrentUrlStateSubscriberImpl(storageHandler),
    CurrentUrlStatePublisher by CurrentUrlStatePublisherImpl(storageHandler),
    UrlStateHolder by UrlStateHolderImpl(savedStateHandle),
    AudioStatusStatePublisher by AudioStatusStatePublisherImpl(storageHandler)