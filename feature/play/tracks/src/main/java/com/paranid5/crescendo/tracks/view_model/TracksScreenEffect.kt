package com.paranid5.crescendo.tracks.view_model

sealed interface TracksScreenEffect {
    data class ShowTrimmer(val trackUri: String) : TracksScreenEffect
    data class ShowMetaEditor(val trackUri: String) : TracksScreenEffect
}
