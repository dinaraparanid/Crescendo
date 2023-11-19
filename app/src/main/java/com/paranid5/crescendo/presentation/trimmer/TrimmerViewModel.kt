package com.paranid5.crescendo.presentation.trimmer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.paranid5.crescendo.data.tracks.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet

class TrimmerViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {
    private companion object {
        private const val TRACK = "track"
        private const val AMPLITUDES = "amplitudes"
    }

    private val _trackState by lazy {
        MutableStateFlow(savedStateHandle.get<Track>(TRACK))
    }

    val trackState = _trackState.asStateFlow()

    fun setTrack(track: Track) {
        savedStateHandle[TRACK] = _trackState.updateAndGet { track }
    }

    private val _amplitudesState by lazy {
        MutableStateFlow(savedStateHandle[AMPLITUDES] ?: listOf<Int>())
    }

    val amplitudesState = _amplitudesState.asStateFlow()

    fun setAmplitudes(amplitudes: List<Int>) {
        savedStateHandle[AMPLITUDES] = _amplitudesState.updateAndGet { amplitudes }
    }
}