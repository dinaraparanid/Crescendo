package com.paranid5.crescendo.feature.stream.fetch.view_model

sealed interface FetchStreamUiIntent {
    data class UpdateUrl(val url: String) : FetchStreamUiIntent
}