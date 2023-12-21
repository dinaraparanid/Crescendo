package com.paranid5.crescendo.presentation.main.trimmer.states

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PlaybackStateHolder(scope: CoroutineScope) : CoroutineScope by scope {
    private val _isPlayerInitializedState by lazy { MutableStateFlow(false) }

    val isPlayerInitializedState by lazy { _isPlayerInitializedState.asStateFlow() }

    fun setPlayerInitialized(isInitialized: Boolean) =
        _isPlayerInitializedState.update { isInitialized }

    private val _isPlayingState by lazy { MutableStateFlow(false) }

    val isPlayingState by lazy { _isPlayingState.asStateFlow() }

    fun setPlaying(isPlaying: Boolean) = _isPlayingState.update { isPlaying }

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