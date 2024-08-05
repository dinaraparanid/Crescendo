package com.paranid5.crescendo.tracks.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.tracks.view_model.TracksUiIntent

@Composable
internal fun SubscribeOnQueryUpdatesEffect(
    searchQuery: String,
    onUiIntent: (TracksUiIntent) -> Unit,
) = LaunchedEffect(searchQuery, onUiIntent) {
    onUiIntent(TracksUiIntent.UpdateSearchQuery(searchQuery))
}