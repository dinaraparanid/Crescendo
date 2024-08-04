package com.paranid5.crescendo.feature.current_playlist.presentation.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistBackResult
import com.paranid5.crescendo.feature.current_playlist.view_model.CurrentPlaylistState

@Composable
internal fun SubscribeOnBackEventsEffect(
    state: CurrentPlaylistState,
    onBack: (CurrentPlaylistBackResult) -> Unit,
) = LaunchedEffect(state.backResult, onBack) {
    state.backResult?.let(onBack)
}
