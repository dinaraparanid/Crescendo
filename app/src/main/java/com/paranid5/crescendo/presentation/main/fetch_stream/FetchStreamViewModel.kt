package com.paranid5.crescendo.presentation.main.fetch_stream

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.presentation.main.fetch_stream.states.AudioStatusStateHolder
import com.paranid5.crescendo.presentation.main.fetch_stream.states.UrlStateHolder

class FetchStreamViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val storageHandler: StorageHandler,
) : ViewModel() {
    val urlStateHolder by lazy {
        UrlStateHolder(savedStateHandle, storageHandler)
    }

    val audioStatusStateHolder by lazy {
        AudioStatusStateHolder(storageHandler)
    }
}