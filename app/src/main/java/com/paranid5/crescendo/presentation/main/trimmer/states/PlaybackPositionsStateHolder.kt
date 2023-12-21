package com.paranid5.crescendo.presentation.main.trimmer.states

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

class PlaybackPositionsStateHolder(private val savedStateHandle: SavedStateHandle) {
    private companion object {
        private const val START_MILLIS = "start_millis"
        private const val END_MILLIS = "end_millis"
    }

    private val _startPosInMillisState by lazy {
        MutableStateFlow(savedStateHandle[START_MILLIS] ?: 0L)
    }

    val startPosInMillisState by lazy { _startPosInMillisState.asStateFlow() }

    fun setStartPosInMillis(startMillis: Long) {
        savedStateHandle[START_MILLIS] = _startPosInMillisState.updateAndGet { startMillis }
    }

    private val _endPosInMillisState by lazy {
        MutableStateFlow(savedStateHandle[END_MILLIS] ?: 0L)
    }

    val endPosInMillisState by lazy { _endPosInMillisState.asStateFlow() }

    fun setEndPosInMillis(endMillis: Long) {
        savedStateHandle[END_MILLIS] = _endPosInMillisState.updateAndGet { endMillis }
    }

    private val _playbackPosInMillisState by lazy {
        MutableStateFlow(startPosInMillisState.value)
    }

    val playbackPosInMillisState by lazy { _playbackPosInMillisState.asStateFlow() }

    fun setPlaybackPosInMillis(position: Long) = _playbackPosInMillisState.update { position }
}