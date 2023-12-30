package com.paranid5.crescendo.presentation.main.trimmer.effects.playback

import com.paranid5.crescendo.presentation.main.trimmer.TrimmerViewModel

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
    resetPlayerStates()
    resetAudioEffects()
}

private fun TrimmerViewModel.resetPlayerStates() {
    setPlaying(false)
    setPlaybackPosInMillis(startPosInMillisState.value)
    setPlayerInitialized(false)
    setPlaybackTaskFinished(false)
}

private fun TrimmerViewModel.resetAudioEffects() {
    setFadeInSecs(0)
    setFadeOutSecs(0)
    setPitch(1F)
    setSpeed(1F)
}

suspend fun TrimmerViewModel.pausePlayback() {
    setPlaying(false)
    resetPlaybackPosition()
}