package com.paranid5.crescendo.presentation.main.trimmer.states

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet

private const val START_MILLIS = "start_millis"
private const val END_MILLIS = "end_millis"
private const val FADE_IN_SECS = "fade_in_secs"
private const val FADE_OUT_SECS = "fade_out_secs"

class PlaybackPositionsStateHolder(private val savedStateHandle: SavedStateHandle) {
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

    private val _fadeInSecsState by lazy { MutableStateFlow(0L) }

    val fadeInSecsState by lazy { _fadeInSecsState.asStateFlow() }

    fun setFadeInSecs(fadeInSecs: Long) {
        savedStateHandle[FADE_IN_SECS] = _fadeInSecsState.updateAndGet { fadeInSecs }
    }

    private val _fadeOutSecsState by lazy { MutableStateFlow(0L) }

    val fadeOutSecsState by lazy { _fadeOutSecsState.asStateFlow() }

    fun setFadeOutSecs(fadeOutSecs: Long) {
        savedStateHandle[FADE_OUT_SECS] = _fadeOutSecsState.updateAndGet { fadeOutSecs }
    }
}