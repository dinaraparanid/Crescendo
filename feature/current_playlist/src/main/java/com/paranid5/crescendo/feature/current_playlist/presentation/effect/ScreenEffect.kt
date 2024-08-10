package com.paranid5.crescendo.feature.current_playlist.presentation.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistScreenEffect
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistState

@Composable
internal fun ScreenEffect(
    state: CurrentPlaylistState,
    onScreenEffect: (CurrentPlaylistScreenEffect) -> Unit,
    onHandled: () -> Unit,
) = LaunchedEffect(state.screenEffect, onScreenEffect, onHandled) {
    state.screenEffect?.let(onScreenEffect)
    onHandled()
}
