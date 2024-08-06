package com.paranid5.crescendo.feature.current_playlist.view_model

sealed interface CurrentPlaylistBackResult {

    data class ShowTrimmer(val trackUri: String) : CurrentPlaylistBackResult
}
