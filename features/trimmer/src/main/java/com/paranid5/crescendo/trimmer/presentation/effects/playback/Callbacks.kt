package com.paranid5.crescendo.trimmer.presentation.effects.playback

import com.paranid5.crescendo.trimmer.presentation.TrimmerViewModel

internal suspend fun TrimmerViewModel.resetPlaybackPosition() {
    while (!isPlaybackTaskFinishedState.value)
        resetPlaybackPosCondVar.wait()

    setPlaybackPosInMillis(startPosInMillisState.value)
    setPlaybackTaskFinished(false)
}

internal suspend fun TrimmerViewModel.notifyPlaybackTaskFinished() {
    setPlaybackTaskFinished(true)
    resetPlaybackPosCondVar.notify()
}

internal fun TrimmerViewModel.resetPlaybackStates() {
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

internal suspend fun TrimmerViewModel.pausePlayback() {
    setPlaying(false)
    resetPlaybackPosition()
}