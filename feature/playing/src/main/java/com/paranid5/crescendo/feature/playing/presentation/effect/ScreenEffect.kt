package com.paranid5.crescendo.feature.playing.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.feature.playing.view_model.PlayingScreenEffect
import com.paranid5.crescendo.feature.playing.view_model.PlayingState

@Composable
internal fun ScreenEffect(
    state: PlayingState,
    onScreenEffect: (PlayingScreenEffect) -> Unit,
    onHandled: () -> Unit,
) = LaunchedEffect(state.screenEffect, onScreenEffect, onHandled) {
    state.screenEffect?.let(onScreenEffect)
    onHandled()
}
