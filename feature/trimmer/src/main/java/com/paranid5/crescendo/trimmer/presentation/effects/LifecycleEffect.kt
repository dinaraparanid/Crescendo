package com.paranid5.crescendo.trimmer.presentation.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.paranid5.crescendo.trimmer.view_model.TrimmerUiIntent

@Composable
internal fun LifecycleEffect(onUiIntent: (TrimmerUiIntent) -> Unit) =
    DisposableEffect(onUiIntent) {
        onUiIntent(TrimmerUiIntent.Lifecycle.OnStart)
        onDispose { onUiIntent(TrimmerUiIntent.Lifecycle.OnStop) }
    }
