package com.paranid5.crescendo.presentation.main.trimmer.effects.playback

import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel
import com.paranid5.crescendo.presentation.main.trimmer.properties.isPlaybackTaskFinishedState
import com.paranid5.crescendo.presentation.main.trimmer.properties.setPlaybackPosInMillis
import com.paranid5.crescendo.presentation.main.trimmer.properties.setPlaybackTaskFinished
import com.paranid5.crescendo.presentation.main.trimmer.properties.setPlayerInitialized
import com.paranid5.crescendo.presentation.main.trimmer.properties.setPlaying
import com.paranid5.crescendo.presentation.main.trimmer.properties.startPosInMillisState

suspend fun TrimmerViewModel.resetPlaybackPosition() {
    while (!isPlaybackTaskFinishedState.value)
        resetPlaybackPosCondVar.wait()

    setPlaybackPosInMillis(startPosInMillisState.value)
    setPlaybackTaskFinished(false)
}

suspend fun TrimmerViewModel.notifyPlaybackTaskFinished() {
    setPlaybackTaskFinished(true)
    resetPlaybackPosCondVar.notify()
}

fun TrimmerViewModel.resetPlaybackStates() {
    setPlaying(false)
    setPlaybackPosInMillis(startPosInMillisState.value)
    setPlayerInitialized(false)
    setPlaybackTaskFinished(false)
}

suspend fun TrimmerViewModel.pausePlayback() {
    setPlaying(false)
    resetPlaybackPosition()
}