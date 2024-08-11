package com.paranid5.crescendo.audio_effects.presentation.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.paranid5.crescendo.audio_effects.view_model.AudioEffectsUiIntent

@Composable
internal fun LifecycleEffect(onUiIntent: (AudioEffectsUiIntent) -> Unit) =
    DisposableEffect(onUiIntent) {
        onUiIntent(AudioEffectsUiIntent.Lifecycle.OnStart)
        onDispose { onUiIntent(AudioEffectsUiIntent.Lifecycle.OnStop) }
    }