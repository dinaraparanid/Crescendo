package com.paranid5.crescendo.tracks.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.paranid5.crescendo.tracks.view_model.TracksUiIntent

@Composable
internal fun LifecycleEffect(
    onUiIntent: (TracksUiIntent) -> Unit,
) = DisposableEffect(onUiIntent) {
    onUiIntent(TracksUiIntent.Lifecycle.OnStart)
    onDispose { onUiIntent(TracksUiIntent.Lifecycle.OnStop) }
}
