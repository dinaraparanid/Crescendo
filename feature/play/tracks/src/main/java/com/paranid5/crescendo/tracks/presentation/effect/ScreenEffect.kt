package com.paranid5.crescendo.tracks.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.tracks.view_model.TracksScreenEffect
import com.paranid5.crescendo.tracks.view_model.TracksState

@Composable
internal fun ScreenEffect(
    state: TracksState,
    onScreenEffect: (TracksScreenEffect) -> Unit,
    onHandled: () -> Unit,
) = LaunchedEffect(state, onScreenEffect, onHandled) {
    state.screenEffect?.let(onScreenEffect)
    onHandled()
}
