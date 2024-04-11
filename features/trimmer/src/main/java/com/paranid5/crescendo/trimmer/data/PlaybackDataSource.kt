package com.paranid5.crescendo.trimmer.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal interface PlaybackDataSource {
    val isPlayerInitializedState: StateFlow<Boolean>
    val isPlaybackTaskFinishedState: StateFlow<Boolean>
    val isPlayingState: StateFlow<Boolean>
    val pitchState: StateFlow<Float>
    val speedState: StateFlow<Float>

    fun setPlayerInitialized(isInitialized: Boolean)
    fun setPlaying(isPlaying: Boolean)
    fun setPitch(pitch: Float)
    fun setSpeed(speed: Float)
    fun launchPlaybackPosMonitorTask(task: suspend () -> Unit)
    fun setPlaybackTaskFinished(isFinished: Boolean)
    fun releasePlaybackPosMonitorTask()
}

internal class PlaybackDataSourceImpl : PlaybackDataSource, CoroutineScope by MainScope() {
    private val _isPlayerInitializedState by lazy {
        MutableStateFlow(false)
    }

    override val isPlayerInitializedState by lazy {
        _isPlayerInitializedState.asStateFlow()
    }

    override fun setPlayerInitialized(isInitialized: Boolean) =
        _isPlayerInitializedState.update { isInitialized }

    private val _isPlayingState by lazy {
        MutableStateFlow(false)
    }

    override val isPlayingState by lazy {
        _isPlayingState.asStateFlow()
    }

    override fun setPlaying(isPlaying: Boolean) =
        _isPlayingState.update { isPlaying }

    private val _pitchState by lazy {
        MutableStateFlow(1F)
    }

    override val pitchState by lazy {
        _pitchState.asStateFlow()
    }

    override fun setPitch(pitch: Float) =
        _pitchState.update { pitch }

    private val _speedState by lazy {
        MutableStateFlow(1F)
    }

    override val speedState by lazy {
        _speedState.asStateFlow()
    }

    override fun setSpeed(speed: Float) =
        _speedState.update { speed }

    private val playbackPosMonitorTaskState by lazy {
        MutableStateFlow<Job?>(null)
    }

    override fun launchPlaybackPosMonitorTask(task: suspend () -> Unit) =
        playbackPosMonitorTaskState.update { launch { task() } }

    private val _isPlaybackTaskFinishedState by lazy {
        MutableStateFlow(false)
    }

    override val isPlaybackTaskFinishedState by lazy {
        _isPlaybackTaskFinishedState.asStateFlow()
    }

    override fun setPlaybackTaskFinished(isFinished: Boolean) =
        _isPlaybackTaskFinishedState.update { isFinished }

    override fun releasePlaybackPosMonitorTask() =
        playbackPosMonitorTaskState.update {
            it?.cancel()
            null
        }
}