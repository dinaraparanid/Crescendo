package com.paranid5.crescendo.playing.view_model

sealed interface PlayingBackResult {

    data object ShowAudioEffects : PlayingBackResult

    data class ShowTrimmer(val trackUri: String) : PlayingBackResult
}
