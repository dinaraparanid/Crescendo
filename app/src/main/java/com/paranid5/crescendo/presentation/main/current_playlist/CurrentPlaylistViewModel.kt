package com.paranid5.crescendo.presentation.main.current_playlist

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.presentation.main.current_playlist.states.CurrentPlaylistStateHolder

class CurrentPlaylistViewModel(private val storageHandler: StorageHandler) : ViewModel() {
    val currentPlaylistStateHolder by lazy {
        CurrentPlaylistStateHolder(storageHandler)
    }
}