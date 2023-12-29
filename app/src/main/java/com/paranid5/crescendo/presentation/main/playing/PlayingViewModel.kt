package com.paranid5.crescendo.presentation.main.playing

import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.presentation.main.playing.states.AudioStatusStateHolder
import com.paranid5.crescendo.presentation.main.playing.states.PlaybackStateHolder

class PlayingViewModel(private val storageHandler: StorageHandler) : ViewModel() {
    val playbackStateHolder by lazy {
        PlaybackStateHolder(storageHandler)
    }

    val audioStatusStateHolder by lazy {
        AudioStatusStateHolder(storageHandler)
    }
}