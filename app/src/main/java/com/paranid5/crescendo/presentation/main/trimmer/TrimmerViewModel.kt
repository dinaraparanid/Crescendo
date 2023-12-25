package com.paranid5.crescendo.presentation.main.trimmer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.data.StorageHandler
import com.paranid5.crescendo.domain.utils.AsyncCondVar
import com.paranid5.crescendo.presentation.main.trimmer.properties.releasePlaybackPosMonitorTask
import com.paranid5.crescendo.presentation.main.trimmer.properties.setAmplitudesAsync
import com.paranid5.crescendo.presentation.main.trimmer.states.WaveformStateHolder
import com.paranid5.crescendo.presentation.main.trimmer.states.PlaybackPositionsStateHolder
import com.paranid5.crescendo.presentation.main.trimmer.states.PlaybackStateHolder
import com.paranid5.crescendo.presentation.main.trimmer.states.TrackStateHolder

class TrimmerViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val storageHandler: StorageHandler
) : ViewModel() {
    val resetPlaybackPosCondVar by lazy { AsyncCondVar() }

    val playbackStateHolder by lazy {
        PlaybackStateHolder(savedStateHandle, viewModelScope)
    }

    val playbackPositionStateHolder by lazy {
        PlaybackPositionsStateHolder(savedStateHandle)
    }

    val trackStateHolder by lazy {
        TrackStateHolder(savedStateHandle)
    }

    val waveformStateHolder by lazy {
        WaveformStateHolder(storageHandler, viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        setAmplitudesAsync(emptyList())
        releasePlaybackPosMonitorTask()
    }
}