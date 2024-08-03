package com.paranid5.crescendo.current_playlist.presentation.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.paranid5.crescendo.current_playlist.presentation.view_model.CurrentPlaylistUiIntent

@Composable
internal fun SubscribeOnLifecycleEffect(onUiIntent: (CurrentPlaylistUiIntent) -> Unit) =
    DisposableEffect(onUiIntent) {
        onUiIntent(CurrentPlaylistUiIntent.OnStart)
        onDispose { onUiIntent(CurrentPlaylistUiIntent.OnStop) }
    }