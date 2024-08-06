package com.paranid5.crescendo.tracks.view_model

sealed interface TracksBackResult {

    data class ShowTrimmer(val trackUri: String) : TracksBackResult
}
