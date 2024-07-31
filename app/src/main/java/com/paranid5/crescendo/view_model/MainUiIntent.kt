package com.paranid5.crescendo.view_model

interface MainUiIntent {
    data class OpenVersionPage(val url: String) : MainUiIntent
    data object DismissVersionDialog : MainUiIntent
}