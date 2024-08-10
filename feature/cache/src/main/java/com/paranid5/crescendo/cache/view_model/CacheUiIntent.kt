package com.paranid5.crescendo.cache.view_model

sealed interface CacheUiIntent {
    data class UpdateDownloadUrl(val url: String) : CacheUiIntent

    data class UpdateFilename(val filename: String) : CacheUiIntent

    data class UpdateSelectedSaveOptionIndex(val selectedSaveOptionIndex: Int) : CacheUiIntent

    data object StartCaching : CacheUiIntent
}
