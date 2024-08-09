package com.paranid5.crescendo.feature.playing.view_model

sealed interface PlayingScreenEffect {

    data object ShowAudioEffects : PlayingScreenEffect

    data object ShowAudioEffectsNotAllowed : PlayingScreenEffect

    data class ShowTrimmer(val trackUri: String) : PlayingScreenEffect

    data object ShowMetaEditor : PlayingScreenEffect
}
