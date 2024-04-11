package com.paranid5.crescendo.trimmer.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal interface PlaybackPositionsDataSource {
    val startPosInMillisState: StateFlow<Long>
    val endPosInMillisState: StateFlow<Long>
    val playbackPosInMillisState: StateFlow<Long>
    val fadeInSecsState: StateFlow<Long>
    val fadeOutSecsState: StateFlow<Long>

    fun setStartPosInMillis(startMillis: Long)
    fun setEndPosInMillis(endMillis: Long)
    fun setPlaybackPosInMillis(position: Long)
    fun setFadeInSecs(fadeInSecs: Long)
    fun setFadeOutSecs(fadeOutSecs: Long)
}

internal class PlaybackPositionsDataSourceImpl : PlaybackPositionsDataSource {
    private val _startPosInMillisState by lazy {
        MutableStateFlow(0L)
    }

    override val startPosInMillisState by lazy {
        _startPosInMillisState.asStateFlow()
    }

    override fun setStartPosInMillis(startMillis: Long) =
        _startPosInMillisState.update { startMillis }

    private val _endPosInMillisState by lazy {
        MutableStateFlow(0L)
    }

    override val endPosInMillisState by lazy {
        _endPosInMillisState.asStateFlow()
    }

    override fun setEndPosInMillis(endMillis: Long) =
        _endPosInMillisState.update { endMillis }

    private val _playbackPosInMillisState by lazy {
        MutableStateFlow(startPosInMillisState.value)
    }

    override val playbackPosInMillisState by lazy {
        _playbackPosInMillisState.asStateFlow()
    }

    override fun setPlaybackPosInMillis(position: Long) =
        _playbackPosInMillisState.update { position }

    private val _fadeInSecsState by lazy { MutableStateFlow(0L) }

    override val fadeInSecsState by lazy {
        _fadeInSecsState.asStateFlow()
    }

    override fun setFadeInSecs(fadeInSecs: Long) =
        _fadeInSecsState.update { fadeInSecs }

    private val _fadeOutSecsState by lazy { MutableStateFlow(0L) }

    override val fadeOutSecsState by lazy {
        _fadeOutSecsState.asStateFlow()
    }

    override fun setFadeOutSecs(fadeOutSecs: Long) =
        _fadeOutSecsState.update { fadeOutSecs }
}