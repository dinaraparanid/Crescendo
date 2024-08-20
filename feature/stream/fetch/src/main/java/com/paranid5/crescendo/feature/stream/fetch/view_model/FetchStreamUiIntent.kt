package com.paranid5.crescendo.feature.stream.fetch.view_model

sealed interface FetchStreamUiIntent {
    data class UpdateUrl(val url: String) : FetchStreamUiIntent

    sealed interface Buttons : FetchStreamUiIntent {
        data object ContinueClick : Buttons
        data object StartStreaming : Buttons
        data object NextClick : Buttons
    }

    data object RefreshCover : FetchStreamUiIntent
}
