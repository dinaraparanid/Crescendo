package com.paranid5.crescendo.presentation.main.trimmer.properties

import com.paranid5.crescendo.domain.trimming.PitchAndSpeed
import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

inline val TrimmerViewModel.isPlayerInitializedState
    get() = playbackStateHolder.isPlayerInitializedState

fun TrimmerViewModel.setPlayerInitialized(isInitialized: Boolean) =
    playbackStateHolder.setPlayerInitialized(isInitialized)

inline val TrimmerViewModel.isPlayingState
    get() = playbackStateHolder.isPlayingState

fun TrimmerViewModel.setPlaying(isPlaying: Boolean) =
    playbackStateHolder.setPlaying(isPlaying)

inline val TrimmerViewModel.pitchState
    get() = playbackStateHolder.pitchState

fun TrimmerViewModel.setPitch(pitch: Float) =
    playbackStateHolder.setPitch(pitch)

inline val TrimmerViewModel.speedState
    get() = playbackStateHolder.speedState

fun TrimmerViewModel.setSpeed(speed: Float) =
    playbackStateHolder.setSpeed(speed)

fun TrimmerViewModel.launchPlaybackPosMonitorTask(task: suspend () -> Unit) =
    playbackStateHolder.launchPlaybackPosMonitorTask(task)

val TrimmerViewModel.isPlaybackTaskFinishedState
    get() = playbackStateHolder.isPlaybackTaskFinishedState

fun TrimmerViewModel.setPlaybackTaskFinished(isFinished: Boolean) =
    playbackStateHolder.setPlaybackTaskFinished(isFinished)

fun TrimmerViewModel.releasePlaybackPosMonitorTask() =
    playbackStateHolder.releasePlaybackPosMonitorTask()

inline val TrimmerViewModel.playbackAlphaFlow
    get() = isPlayingState.map { if (it) 1F else 0F }

inline val TrimmerViewModel.pitchAndSpeedFlow
    get() = combine(pitchState, speedState) { pitch, speed ->
        PitchAndSpeed(pitch, speed)
    }