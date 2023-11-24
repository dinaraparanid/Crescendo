package com.paranid5.crescendo.presentation.main.trimmer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paranid5.crescendo.data.tracks.Track
import com.paranid5.crescendo.domain.StorageHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

class TrimmerViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val storageHandler: StorageHandler
) : ViewModel() {
    private companion object {
        private const val TRACK = "track"
        private const val START_MILLIS = "start_millis"
        private const val END_MILLIS = "end_millis"
        private const val IS_PLAYING_STATE = "is_playing"
    }

    private val _trackState by lazy {
        MutableStateFlow(savedStateHandle.get<Track>(TRACK))
    }

    val trackState = _trackState.asStateFlow()

    fun setTrack(track: Track) {
        savedStateHandle[TRACK] = _trackState.updateAndGet { track }
    }

    val amplitudesState by lazy { storageHandler.amplitudesState }

    fun setAmplitudesAsync(amplitudes: List<Int>) = viewModelScope.launch(Dispatchers.IO) {
        storageHandler.storeAmplitudes(amplitudes)
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

    val trimmedDurationFlow by lazy {
        combine(
            startPosInMillisState,
            endPosInMillisState
        ) { startMillis, endMillis -> endMillis - startMillis }
    }

    private val _isPlayingState by lazy { MutableStateFlow(false) }

    val isPlayingState by lazy { _isPlayingState.asStateFlow() }

    fun setPlaying(isPlaying: Boolean) {
        savedStateHandle[IS_PLAYING_STATE] = _isPlayingState.updateAndGet { isPlaying }
    }

    override fun onCleared() {
        super.onCleared()
        setAmplitudesAsync(emptyList())
    }
}