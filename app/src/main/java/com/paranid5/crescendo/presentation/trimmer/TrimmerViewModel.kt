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
        private const val START_MILLIS = "start_millis"
        private const val END_MILLIS = "end_millis"
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

    private val _startPosInMillisState by lazy {
        MutableStateFlow(savedStateHandle[START_MILLIS] ?: 0L)
    }

    val startPosInMillisState by lazy { _startPosInMillisState.asStateFlow() }

    fun setStartPosInMillis(startMillis: Long) {
        savedStateHandle[START_MILLIS] = _startPosInMillisState.updateAndGet { startMillis }
    }

    private val _endPosInMillisState by lazy {
        MutableStateFlow(
            savedStateHandle[END_MILLIS]
                ?: trackState.value?.duration
                ?: 0L
        )
    }

    val endPosInMillisState by lazy { _endPosInMillisState.asStateFlow() }

    fun setEndPosInMillis(endMillis: Long) {
        savedStateHandle[END_MILLIS] = _endPosInMillisState.updateAndGet { endMillis }
    }
}