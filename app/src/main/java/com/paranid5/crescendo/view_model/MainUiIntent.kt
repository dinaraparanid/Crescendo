package com.paranid5.crescendo.view_model

internal interface MainUiIntent {
    data class OpenVersionPage(val url: String) : MainUiIntent
    data object DismissVersionDialog : MainUiIntent
}
