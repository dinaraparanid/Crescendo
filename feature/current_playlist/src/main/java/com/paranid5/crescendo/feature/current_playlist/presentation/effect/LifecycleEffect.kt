package com.paranid5.crescendo.feature.current_playlist.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistUiIntent

@Composable
internal fun LifecycleEffect(onUiIntent: (CurrentPlaylistUiIntent) -> Unit) =
    DisposableEffect(onUiIntent) {
        onUiIntent(CurrentPlaylistUiIntent.Lifecycle.OnStart)
        onDispose { onUiIntent(CurrentPlaylistUiIntent.Lifecycle.OnStop) }
    }