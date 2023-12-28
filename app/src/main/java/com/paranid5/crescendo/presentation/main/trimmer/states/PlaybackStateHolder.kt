package com.paranid5.crescendo.presentation.main.trimmer.states

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

private const val PITCH = "pitch"
private const val SPEED = "speed"

class PlaybackStateHolder(private val savedStateHandle: SavedStateHandle) :
    CoroutineScope by MainScope() {
    private val _isPlayerInitializedState by lazy { MutableStateFlow(false) }

    val isPlayerInitializedState by lazy { _isPlayerInitializedState.asStateFlow() }

    fun setPlayerInitialized(isInitialized: Boolean) =
        _isPlayerInitializedState.update { isInitialized }

    private val _isPlayingState by lazy { MutableStateFlow(false) }

    val isPlayingState by lazy { _isPlayingState.asStateFlow() }

    fun setPlaying(isPlaying: Boolean) = _isPlayingState.update { isPlaying }

    private val _pitchState by lazy { MutableStateFlow(1F) }

    val pitchState by lazy { _pitchState.asStateFlow() }

    fun setPitch(pitch: Float) {
        savedStateHandle[PITCH] = _pitchState.updateAndGet { pitch }
    }

    private val _speedState by lazy { MutableStateFlow(1F) }

    val speedState by lazy { _speedState.asStateFlow() }

    fun setSpeed(speed: Float) {
        savedStateHandle[SPEED] = _speedState.updateAndGet { speed }
    }

    private val playbackPosMonitorTaskState by lazy { MutableStateFlow<Job?>(null) }

    fun launchPlaybackPosMonitorTask(task: suspend () -> Unit) =
        playbackPosMonitorTaskState.update {
            launch { task() }
        }

    private val _isPlaybackTaskFinishedState by lazy { MutableStateFlow(false) }

    val isPlaybackTaskFinishedState by lazy { _isPlaybackTaskFinishedState.asStateFlow() }

    fun setPlaybackTaskFinished(isFinished: Boolean) =
        _isPlaybackTaskFinishedState.update { isFinished }

    fun releasePlaybackPosMonitorTask() {
        playbackPosMonitorTaskState.update {
            it?.cancel()
            null
        }
    }
}