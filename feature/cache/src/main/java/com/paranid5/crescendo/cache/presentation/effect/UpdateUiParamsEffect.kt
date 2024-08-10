package com.paranid5.crescendo.cache.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.cache.view_model.CacheUiIntent

@Composable
internal fun UpdateUrlEffect(
    url: String,
    onUiIntent: (CacheUiIntent) -> Unit,
) = LaunchedEffect(url, onUiIntent) {
    onUiIntent(CacheUiIntent.UpdateDownloadUrl(url = url))
}
