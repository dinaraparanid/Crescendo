package com.paranid5.crescendo.feature.current_playlist.view_model

sealed interface CurrentPlaylistScreenEffect {

    data class ShowTrimmer(val trackUri: String) : CurrentPlaylistScreenEffect
    data object ShowMetaEditor : CurrentPlaylistScreenEffect
}
