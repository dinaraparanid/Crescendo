package com.paranid5.crescendo.feature.play.main.view_model

sealed interface PlayBackResult {

    data class ShowTrimmer(val trackUri: String) : PlayBackResult
}
