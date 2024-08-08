package com.paranid5.crescendo.feature.playing.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.paranid5.crescendo.feature.playing.view_model.PlayingUiIntent

@Composable
internal fun LifecycleEffect(onUiIntent: (PlayingUiIntent) -> Unit) =
    DisposableEffect(onUiIntent) {
        onUiIntent(PlayingUiIntent.Lifecycle.OnStart)
        onDispose { PlayingUiIntent.Lifecycle.OnStop }
    }
